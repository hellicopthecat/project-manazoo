package app.common;

import java.sql.*;

import app.common.exception.IdGenerationException;
import app.config.DatabaseConnection;

/**
 * 데이터베이스 기반 ID 생성 유틸리티 클래스입니다.
 * 호출자 클래스를 자동 감지하여 적절한 접두사로 고유한 ID를 생성합니다.
 * 
 * <p><strong>필요한 데이터베이스 테이블:</strong>
 * <pre>{@code
 * CREATE TABLE id_generator (
 *     prefix VARCHAR(10) PRIMARY KEY,
 *     last_number INT NOT NULL
 * );
 * }</pre>
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
     * 각 타입별로 고유한 접두사를 가지며, id_generator 테이블의 prefix 컬럼과 매핑됩니다.
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
     * @throws IdGenerationException 지원되지 않는 클래스에서 호출된 경우
     */
    private static IdType determineIdType() throws IdGenerationException {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            String fullClassName = element.getClassName();
            
            // 패키지 경로를 제거하고 클래스명만 추출
            String simpleClassName = getSimpleClassName(fullClassName);
            
            // 정확한 클래스명 매칭
            if ("EnclosureManager".equals(simpleClassName)) {
                return IdType.ENCLOSURE;
            } else if ("AnimalManager".equals(simpleClassName)) {
                return IdType.ANIMAL;
            } else if ("ZooKeeperManager".equals(simpleClassName)) {
                return IdType.ZOOKEEPER;
            } else if ("FinanceManager".equals(simpleClassName)) {
                return IdType.FINANCE;
            } else if ("VisitorManager".equals(simpleClassName)) {
                return IdType.VISITOR;
            }
        }

        // 지원되지 않는 클래스에서 호출된 경우
        String callerInfo = getCallerDebugInfo(stackTrace);
        throw new IdGenerationException(
            "ID 생성 요청이 지원되지 않는 클래스에서 호출되었습니다. " +
            "지원 클래스: EnclosureManager, AnimalManager, ZooKeeperManager, FinanceManager, VisitorManager. " +
            "호출 위치: " + callerInfo
        );
    }

    /**
     * 패키지 경로를 제거하고 단순 클래스명만 반환합니다.
     */
    private static String getSimpleClassName(String fullClassName) {
        int lastDotIndex = fullClassName.lastIndexOf('.');
        return lastDotIndex >= 0 ? fullClassName.substring(lastDotIndex + 1) : fullClassName;
    }

    /**
     * 디버깅을 위한 호출자 정보를 생성합니다.
     */
    private static String getCallerDebugInfo(StackTraceElement[] stackTrace) {
        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3]; // generateId() 호출자
            return String.format("%s.%s():%d", 
                getSimpleClassName(caller.getClassName()), 
                caller.getMethodName(), 
                caller.getLineNumber());
        }
        return "알 수 없는 호출자";
    }

    // ==================== 핵심 ID 생성 메서드 ====================

    /**
     * 호출자를 자동 감지하여 적절한 ID를 생성합니다.
     * 
     * <p>스택 트레이스를 분석하여 호출한 Manager 클래스를 확인하고, 
     * 해당 타입에 맞는 ID를 데이터베이스 기반으로 안전하게 생성합니다.
     *
     * @return 생성된 고유 ID (예: "E-0001")
     * @throws IdGenerationException 지원되지 않는 클래스에서 호출된 경우 또는 데이터베이스 오류 발생 시
     */
    public static String generateId() throws IdGenerationException {
        // 1. 호출자 클래스를 자동으로 감지하여 ID 타입 결정
        IdType idType = determineIdType();
        String prefix = idType.getPrefix();
        
        // 2. 데이터베이스 기반으로 안전한 ID 생성
        return generateIdWithPrefix(prefix);
    }

    /**
     * 지정된 접두사로 고유한 ID를 데이터베이스 기반으로 생성합니다.
     * 
     * @param prefix ID 접두사
     * @return 생성된 고유 ID (예: "A-0001")
     * @throws IdGenerationException 데이터베이스 오류 발생 시
     */
    private static String generateIdWithPrefix(String prefix) throws IdGenerationException {
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
                // 오류 발생 시 롤백
                connection.rollback();
                throw new IdGenerationException("ID 생성 중 데이터베이스 오류 발생 (접두사: " + prefix + ")", e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new IdGenerationException("데이터베이스 연결 실패", e);
        }
    }

    // ==================== 내부 헬퍼 메서드들 ====================

    /**
     * 새로운 접두사를 id_generator 테이블에 삽입합니다.
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
     * @param prefix ID 접두사
     * @param number 번호 (1부터 시작)
     * @return 포맷된 ID 문자열 (예: A-0001)
     */
    private static String formatId(String prefix, int number) {
        return String.format("%s-%04d", prefix, number);
    }
}
