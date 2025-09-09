package app.common;

import java.sql.*;
import app.config.DatabaseConnection;

/**
 * 데이터베이스 기반 ID 생성 유틸리티 클래스입니다.
 * 호출자 클래스를 자동 감지하여 적절한 접두사로 고유한 ID를 생성합니다.
 * 
 * <p>지원되는 Manager 클래스:
 * <ul>
 *   <li>EnclosureManager: E-0001, E-0002, E-0003, ...</li>
 *   <li>AnimalManager: A-0001, A-0002, A-0003, ...</li>
 *   <li>ZooKeeperManager: K-0001, K-0002, K-0003, ...</li>
 *   <li>FinanceManager: F-0001, F-0002, F-0003, ...</li>
 *   <li>VisitorManager: V-0001, V-0002, V-0003, ...</li>
 * </ul>
 * 
 * <p>사용법:
 * <pre>{@code
 * // EnclosureManager.java에서 호출
 * String id = DatabaseIdGenerator.generateId(); // "E-0001" 반환
 * }</pre>
 */
public final class DatabaseIdGenerator {

    /**
     * ID 타입을 정의하는 열거형입니다.
     * 각 타입별로 고유한 접두사를 가집니다.
     */
    public enum IdType {
        /** 사육장 타입 (접두사: E) */
        ENCLOSURE("E"),
        /** 동물 타입 (접두사: A) */
        ANIMAL("A"),
        /** 사육사 타입 (접두사: K) */
        ZOOKEEPER("K"),
        /** 재정 타입 (접두사: F) */
        FINANCE("F"),
        /** 방문객 타입 (접두사: V) */
        VISITOR("V");

        private final String prefix;

        IdType(String prefix) {
            this.prefix = prefix;
        }

        /**
         * ID 접두사를 반환합니다.
         * @return ID 접두사 문자열
         */
        public String getPrefix() {
            return prefix;
        }
    }

    private DatabaseIdGenerator() {
        // 유틸리티 클래스이므로 인스턴스 생성 방지
    }

    // ==================== 호출자 자동 감지 메서드 ====================

    /**
     * 스택 트레이스를 분석하여 호출자 클래스를 기반으로 ID 타입을 자동 결정합니다.
     * 
     * @return 결정된 ID 타입
     * @throws IllegalStateException 지원되지 않는 클래스에서 호출된 경우
     */
    private static IdType determineIdType() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();

            if (className.contains("EnclosureManager")) {
                return IdType.ENCLOSURE;
            } else if (className.contains("AnimalManager")) {
                return IdType.ANIMAL;
            } else if (className.contains("ZooKeeperManager")) {
                return IdType.ZOOKEEPER;
            } else if (className.contains("FinanceManager")) {
                return IdType.FINANCE;
            } else if (className.contains("VisitorManager")) {
                return IdType.VISITOR;
            }
        }

        throw new IllegalStateException("ID 생성 요청이 지원되지 않는 클래스에서 호출되었습니다. "
                + "EnclosureManager, AnimalManager, ZooKeeperManager, FinanceManager, VisitorManager에서만 호출 가능합니다.");
    }

    // ==================== 핵심 ID 생성 메서드 ====================

    /**
     * 호출자를 자동 감지하여 적절한 ID를 생성합니다.
     * 
     * <p>스택 트레이스를 분석하여 호출한 Manager 클래스를 확인하고, 
     * 해당 타입에 맞는 ID를 데이터베이스 기반으로 안전하게 생성합니다.
     * 
     * <p>생성 규칙:
     * <ul>
     *   <li>EnclosureManager → E-0001, E-0002, ...</li>
     *   <li>AnimalManager → A-0001, A-0002, ...</li>
     *   <li>ZooKeeperManager → K-0001, K-0002, ...</li>
     *   <li>FinanceManager → F-0001, F-0002, ...</li>
     *   <li>VisitorManager → V-0001, V-0002, ...</li>
     * </ul>
     *
     * @return 생성된 고유 ID (예: "E-0001")
     * @throws IllegalStateException 지원되지 않는 클래스에서 호출된 경우
     * @throws RuntimeException 데이터베이스 오류 발생 시
     */
    public static String generateId() {
        // 1. 호출자 클래스를 자동으로 감지하여 ID 타입 결정
        IdType idType = determineIdType();
        String prefix = idType.getPrefix();
        
        // 2. 데이터베이스 기반으로 안전한 ID 생성
        return generateIdWithPrefix(prefix);
    }

    /**
     * 지정된 접두사로 고유한 ID를 데이터베이스 기반으로 생성합니다.
     * 
     * @param prefix ID 접두사 (예: "A", "E", "K")
     * @return 생성된 고유 ID (예: "A-0001")
     * @throws RuntimeException 데이터베이스 오류 발생 시
     */
    private static String generateIdWithPrefix(String prefix) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // 현재 번호 조회 및 행 잠금 (동시성 보장)
                String selectSql = "SELECT last_number FROM id_generator WHERE prefix = ? FOR UPDATE";
                
                int lastNumber;
                try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                    selectStmt.setString(1, prefix);
                    
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (rs.next()) {
                            lastNumber = rs.getInt("last_number");
                        } else {
                            // 접두사가 없으면 새로 생성 (초기값 0)
                            lastNumber = 0;
                            insertNewPrefix(connection, prefix, lastNumber);
                        }
                    }
                }

                // 번호 증가 및 업데이트
                int newNumber = lastNumber + 1;
                updateLastNumber(connection, prefix, newNumber);

                // 트랜잭션 커밋
                connection.commit();
                
                // 포맷된 ID 반환
                return formatId(prefix, newNumber);

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("ID 생성 중 데이터베이스 오류 발생 (접두사: " + prefix + "): " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
        }
    }

    // ==================== 내부 헬퍼 메서드들 ====================

    /**
     * 새로운 접두사를 id_generator 테이블에 삽입합니다.
     * 
     * @param connection 데이터베이스 연결
     * @param prefix 접두사
     * @param initialNumber 초기 번호
     * @throws SQLException 데이터베이스 오류 발생 시
     */
    private static void insertNewPrefix(Connection connection, String prefix, int initialNumber) 
            throws SQLException {
        String insertSql = "INSERT INTO id_generator (prefix, last_number) VALUES (?, ?)";
        
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setString(1, prefix);
            insertStmt.setInt(2, initialNumber);
            
            int affected = insertStmt.executeUpdate();
            if (affected != 1) {
                throw new SQLException("새로운 접두사 삽입 실패: " + prefix);
            }
        }
    }

    /**
     * 지정된 접두사의 마지막 번호를 업데이트합니다.
     * 
     * @param connection 데이터베이스 연결
     * @param prefix 접두사
     * @param newNumber 새로운 번호
     * @throws SQLException 데이터베이스 오류 발생 시
     */
    private static void updateLastNumber(Connection connection, String prefix, int newNumber) 
            throws SQLException {
        String updateSql = "UPDATE id_generator SET last_number = ? WHERE prefix = ?";
        
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setInt(1, newNumber);
            updateStmt.setString(2, prefix);
            
            int affected = updateStmt.executeUpdate();
            if (affected != 1) {
                throw new SQLException("번호 업데이트 실패: " + prefix);
            }
        }
    }

    /**
     * 접두사와 번호를 결합하여 포맷된 ID를 생성합니다.
     * 
     * @param prefix 접두사
     * @param number 번호
     * @return 포맷된 ID (예: "A-0001")
     */
    private static String formatId(String prefix, int number) {
        return String.format("%s-%04d", prefix, number);
    }
}
