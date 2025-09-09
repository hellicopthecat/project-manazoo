package app.repository.jdbc;

import app.common.SimpleLogger;
import app.config.DatabaseConnection;
import app.enclosure.Enclosure;
import app.enclosure.EnvironmentType;
import app.enclosure.LocationType;
import app.repository.interfaces.EnclosureRepository;

import java.sql.*;
import java.util.*;

/**
 * JDBC 기반 사육장 Repository 구현체입니다.
 * MemoryEnclosureRepository와 동일한 기능을 데이터베이스 기반으로 제공합니다.
 * 
 * <p><strong>설계 특징:</strong></p>
 * <ul>
 *   <li>단순화된 관계 설계: animals.enclosure_id 외래키만 사용 (중간 테이블 제거)</li>
 *   <li>enclosure_caretakers는 다대다 관계로 유지</li>
 *   <li>Singleton 패턴 적용으로 인스턴스 관리</li>
 *   <li>트랜잭션 관리로 데이터 일관성 보장</li>
 * </ul>
 * 
 * <p><strong>주요 기능:</strong></p>
 * <ul>
 *   <li>사육장 CRUD 연산</li>
 *   <li>동물 배치 관리 (animals.enclosure_id 업데이트)</li>
 *   <li>사육사 배정 관리 (enclosure_caretakers 테이블)</li>
 *   <li>복합 조건 검색 (환경/위치별 조회)</li>
 *   <li>수용 가능 사육장 조회</li>
 * </ul>
 * 
 * @see EnclosureRepository 인터페이스 정의
 * @see DatabaseConnection 데이터베이스 연결 관리
 * @see SimpleLogger 로깅 시스템
 * @since 1.0
 */
public class JdbcEnclosureRepository implements EnclosureRepository {

    // 로거 인스턴스
    private static final SimpleLogger logger = SimpleLogger.getLogger(JdbcEnclosureRepository.class);

    // ==================== Singleton 패턴 구현 ====================
    
    private static class SingletonHolder {
        private static final JdbcEnclosureRepository INSTANCE = new JdbcEnclosureRepository();
    }
    
    private JdbcEnclosureRepository() {
        // private 생성자로 외부 인스턴스 생성 방지
    }
    
    /**
     * JdbcEnclosureRepository의 싱글톤 인스턴스를 반환합니다.
     * 
     * <p><strong>Singleton 패턴 사용 이유:</strong></p>
     * <ul>
     *   <li>메모리 효율성: 하나의 Repository 인스턴스만 생성</li>
     *   <li>일관성: 모든 사육장 관련 작업이 동일한 인스턴스를 통해 처리</li>
     *   <li>스레드 안전성: Lazy 초기화로 멀티스레드 환경에서 안전</li>
     * </ul>
     * 
     * @return JdbcEnclosureRepository 싱글톤 인스턴스
     */
    public static JdbcEnclosureRepository getInstance() {
        logger.debug("JdbcEnclosureRepository 싱글톤 인스턴스 반환");
        return SingletonHolder.INSTANCE;
    }

    // ==================== MemoryEnclosureRepository와 동일한 기능들 ====================

    /**
     * 사육장을 데이터베이스에 저장합니다.
     * 
     * <p><strong>저장 과정:</strong></p>
     * <ol>
     *   <li>기본 사육장 정보 저장 (enclosures 테이블)</li>
     *   <li>거주 동물 정보 저장 (animals.enclosure_id 업데이트)</li>
     *   <li>배정된 사육사 정보 저장 (enclosure_caretakers 테이블)</li>
     * </ol>
     * 
     * <p><strong>트랜잭션 처리:</strong><br>
     * 모든 저장 작업이 하나의 트랜잭션으로 처리되어 데이터 일관성을 보장합니다.
     * 중간에 오류가 발생하면 전체 작업이 롤백됩니다.</p>
     * 
     * @param enclosure 저장할 사육장 객체 (null이면 IllegalArgumentException 발생)
     * @return 저장된 사육장 객체 (동일한 인스턴스 반환)
     * @throws IllegalArgumentException enclosure가 null이거나 ID가 null인 경우
     * @throws RuntimeException 데이터베이스 저장 중 오류 발생 시
     */
    @Override
    public Enclosure save(Enclosure enclosure) {
        if (enclosure == null) {
            logger.error("저장할 사육장 객체가 null입니다");
            throw new IllegalArgumentException("인클로저는 null일 수 없습니다.");
        }
        if (enclosure.getId() == null) {
            logger.error("사육장 ID가 null입니다: " + enclosure.getName());
            throw new IllegalArgumentException("인클로저 ID는 null일 수 없습니다.");
        }

        logger.debug("사육장 저장 시작: ID=%s, 이름=%s", enclosure.getId(), enclosure.getName());

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            logger.debug("트랜잭션 시작: 사육장 저장");

            try {
                // 기본 사육장 정보 저장
                saveEnclosureBasicInfo(connection, enclosure);
                logger.debug("기본 사육장 정보 저장 완료: %s", enclosure.getId());

                // 관계 데이터 저장
                saveEnclosureAnimals(connection, enclosure);
                saveEnclosureCaretakers(connection, enclosure);

                connection.commit();
                return enclosure;

            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("사육장 저장 중 오류 발생: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ID로 인클로저를 조회합니다.
     *
     * @param id 인클로저 ID
     * @return 조회된 인클로저 (Optional로 래핑됨)
     */
    @Override
    public Optional<Enclosure> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = """
            SELECT id, name, area_size, temperature, location_type, environment_type, 
                   created_at, updated_at
            FROM enclosures 
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Enclosure enclosure = mapResultSetToEnclosure(rs);
                    
                    // 관계 데이터 로딩
                    loadEnclosureAnimals(connection, enclosure);
                    loadEnclosureCaretakers(connection, enclosure);
                    
                    return Optional.of(enclosure);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("사육장 조회 중 오류 발생 (ID: " + id + "): " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * 모든 인클로저를 조회합니다.
     *
     * @return 모든 인클로저의 리스트
     */
    @Override
    public List<Enclosure> findAll() {
        List<Enclosure> enclosures = new ArrayList<>();
        
        String sql = """
            SELECT id, name, area_size, temperature, location_type, environment_type, 
                   created_at, updated_at
            FROM enclosures 
            ORDER BY created_at DESC
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Enclosure enclosure = mapResultSetToEnclosure(rs);
                
                // 관계 데이터 로딩
                loadEnclosureAnimals(connection, enclosure);
                loadEnclosureCaretakers(connection, enclosure);
                
                enclosures.add(enclosure);
            }

        } catch (SQLException e) {
            throw new RuntimeException("사육장 목록 조회 중 오류 발생: " + e.getMessage(), e);
        }

        return enclosures;
    }

    /**
     * 인클로저를 업데이트합니다.
     *
     * @param enclosure 업데이트할 인클로저
     * @return 업데이트된 인클로저
     */
    @Override
    public Enclosure update(Enclosure enclosure) {
        if (enclosure == null) {
            throw new IllegalArgumentException("인클로저는 null일 수 없습니다.");
        }
        if (enclosure.getId() == null) {
            throw new IllegalArgumentException("인클로저 ID는 null일 수 없습니다.");
        }
        if (!existsById(enclosure.getId())) {
            throw new IllegalArgumentException("업데이트할 인클로저가 존재하지 않습니다: " + enclosure.getId());
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // 기본 정보 업데이트
                updateEnclosureBasicInfo(connection, enclosure);

                // 관계 데이터 재설정
                deleteEnclosureRelations(connection, enclosure.getId());
                saveEnclosureAnimals(connection, enclosure);
                saveEnclosureCaretakers(connection, enclosure);

                connection.commit();
                return enclosure;

            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("사육장 업데이트 중 오류 발생: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ID로 인클로저를 삭제합니다.
     *
     * @param id 삭제할 인클로저 ID
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // 관련 동물들의 enclosure_id를 NULL로 설정 (대기 상태로 변경)
                unassignAnimalsFromEnclosure(connection, id);
                
                // 사육장 삭제 (CASCADE로 enclosure_caretakers도 자동 삭제됨)
                String sql = "DELETE FROM enclosures WHERE id = ?";
                
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, id);
                    int affected = stmt.executeUpdate();
                    
                    connection.commit();
                    return affected > 0;
                }

            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("사육장 삭제 중 오류 발생: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("사육장 삭제 중 데이터베이스 연결 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 인클로저가 존재하는지 확인합니다.
     *
     * @param id 확인할 인클로저 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }

        String sql = "SELECT 1 FROM enclosures WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("사육장 존재 확인 중 오류 발생 (ID: " + id + "): " + e.getMessage(), e);
        }
    }

    /**
     * 모든 인클로저를 삭제합니다.
     */
    @Override
    public void deleteAll() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // 모든 동물들의 enclosure_id를 NULL로 설정
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE animals SET enclosure_id = NULL")) {
                    stmt.executeUpdate();
                }
                
                // 모든 사육장 삭제
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM enclosures")) {
                    stmt.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("모든 사육장 삭제 중 오류 발생: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("모든 사육장 삭제 중 데이터베이스 연결 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 총 인클로저 개수를 반환합니다.
     *
     * @return 인클로저 개수
     */
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM enclosures";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("사육장 개수 조회 중 오류 발생: " + e.getMessage(), e);
        }

        return 0;
    }

    /**
     * 환경 타입별로 인클로저를 조회합니다.
     *
     * @param environmentType 환경 타입
     * @return 해당 환경 타입의 인클로저 리스트
     */
    @Override
    public List<Enclosure> findByEnvironmentType(EnvironmentType environmentType) {
        if (environmentType == null) {
            return new ArrayList<>();
        }

        List<Enclosure> enclosures = new ArrayList<>();
        String sql = """
            SELECT id, name, area_size, temperature, location_type, environment_type, 
                   created_at, updated_at
            FROM enclosures 
            WHERE environment_type = ?
            ORDER BY name
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, environmentType.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enclosure enclosure = mapResultSetToEnclosure(rs);
                    loadEnclosureAnimals(connection, enclosure);
                    loadEnclosureCaretakers(connection, enclosure);
                    enclosures.add(enclosure);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("환경 타입별 사육장 조회 중 오류 발생: " + e.getMessage(), e);
        }

        return enclosures;
    }

    /**
     * 위치 타입별로 인클로저를 조회합니다.
     *
     * @param locationType 위치 타입
     * @return 해당 위치 타입의 인클로저 리스트
     */
    @Override
    public List<Enclosure> findByLocationType(LocationType locationType) {
        if (locationType == null) {
            return new ArrayList<>();
        }

        List<Enclosure> enclosures = new ArrayList<>();
        String sql = """
            SELECT id, name, area_size, temperature, location_type, environment_type, 
                   created_at, updated_at
            FROM enclosures 
            WHERE location_type = ?
            ORDER BY name
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, locationType.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enclosure enclosure = mapResultSetToEnclosure(rs);
                    loadEnclosureAnimals(connection, enclosure);
                    loadEnclosureCaretakers(connection, enclosure);
                    enclosures.add(enclosure);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("위치 타입별 사육장 조회 중 오류 발생: " + e.getMessage(), e);
        }

        return enclosures;
    }

    /**
     * 현재 수용 가능한 인클로저를 조회합니다.
     * max_capacity 컬럼이 제거되어 모든 사육장이 수용 가능한 것으로 처리됩니다.
     *
     * @return 모든 인클로저 리스트
     */
    @Override
    public List<Enclosure> findAvailableEnclosures() {
        // max_capacity 컬럼이 제거되어 모든 사육장이 수용 가능한 것으로 처리
        return findAll();
    }

    /**
     * 특정 환경과 위치 조건을 모두 만족하는 인클로저를 조회합니다.
     *
     * @param environmentType 환경 타입
     * @param locationType 위치 타입
     * @return 조건에 맞는 인클로저 리스트
     */
    @Override
    public List<Enclosure> findByEnvironmentTypeAndLocationType(EnvironmentType environmentType, LocationType locationType) {
        if (environmentType == null || locationType == null) {
            return new ArrayList<>();
        }

        List<Enclosure> enclosures = new ArrayList<>();
        String sql = """
            SELECT id, name, area_size, temperature, location_type, environment_type, 
                   created_at, updated_at
            FROM enclosures 
            WHERE environment_type = ? AND location_type = ?
            ORDER BY name
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, environmentType.name());
            stmt.setString(2, locationType.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enclosure enclosure = mapResultSetToEnclosure(rs);
                    loadEnclosureAnimals(connection, enclosure);
                    loadEnclosureCaretakers(connection, enclosure);
                    enclosures.add(enclosure);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("환경/위치 타입별 사육장 조회 중 오류 발생: " + e.getMessage(), e);
        }

        return enclosures;
    }

    // ==================== 내부 헬퍼 메서드들 ====================

    private void saveEnclosureBasicInfo(Connection connection, Enclosure enclosure) throws SQLException {
        String sql = """
            INSERT INTO enclosures (id, name, area_size, temperature, location_type, 
                                   environment_type)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, enclosure.getId());
            stmt.setString(2, enclosure.getName());
            stmt.setFloat(3, enclosure.getAreaSize());
            stmt.setFloat(4, enclosure.getTemperature());
            stmt.setString(5, enclosure.getLocationType().name());
            stmt.setString(6, enclosure.getEnvironmentType().name());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("사육장 저장에 실패했습니다: " + enclosure.getId());
            }
        }
    }

    private void updateEnclosureBasicInfo(Connection connection, Enclosure enclosure) throws SQLException {
        String sql = """
            UPDATE enclosures 
            SET name = ?, area_size = ?, temperature = ?, location_type = ?, 
                environment_type = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, enclosure.getName());
            stmt.setFloat(2, enclosure.getAreaSize());
            stmt.setFloat(3, enclosure.getTemperature());
            stmt.setString(4, enclosure.getLocationType().name());
            stmt.setString(5, enclosure.getEnvironmentType().name());
            stmt.setString(6, enclosure.getId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("사육장 업데이트에 실패했습니다: " + enclosure.getId());
            }
        }
    }

    /**
     * 동물들을 사육장에 배정합니다 (animals.enclosure_id 업데이트).
     */
    private void saveEnclosureAnimals(Connection connection, Enclosure enclosure) throws SQLException {
        if (enclosure.getAllInhabitants() == null || enclosure.getAllInhabitants().isEmpty()) {
            return;
        }

        String sql = "UPDATE animals SET enclosure_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String animalId : enclosure.getAllInhabitants().keySet()) {
                stmt.setString(1, enclosure.getId());
                stmt.setString(2, animalId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * 사육사들을 사육장에 배정합니다 (enclosure_caretakers 테이블).
     * 표준 SQL을 사용하여 중복 배정을 방지합니다.
     */
    private void saveEnclosureCaretakers(Connection connection, Enclosure enclosure) throws SQLException {
        if (enclosure.getAllCaretakers() == null || enclosure.getAllCaretakers().isEmpty()) {
            return;
        }

        logger.debug("사육사 배정 시작: 사육장 ID=%s, 사육사 수=%d", 
                    enclosure.getId(), enclosure.getAllCaretakers().size());

        // 표준 SQL을 사용한 중복 방지 로직
        String checkSql = "SELECT 1 FROM enclosure_caretakers WHERE enclosure_id = ? AND keeper_id = ?";
        String insertSql = "INSERT INTO enclosure_caretakers (enclosure_id, keeper_id) VALUES (?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql);
             PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

            for (String keeperId : enclosure.getAllCaretakers().keySet()) {
                // 중복 여부 확인
                checkStmt.setString(1, enclosure.getId());
                checkStmt.setString(2, keeperId);
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) { // 중복되지 않은 경우에만 삽입
                        insertStmt.setString(1, enclosure.getId());
                        insertStmt.setString(2, keeperId);
                        insertStmt.addBatch();
                        
                        logger.debug("사육사 배정 추가: 사육장=%s, 사육사=%s", 
                                    enclosure.getId(), keeperId);
                    } else {
                        logger.debug("이미 배정된 사육사 건너뜀: 사육장=%s, 사육사=%s", 
                                    enclosure.getId(), keeperId);
                    }
                }
            }
            
            // 배치 실행
            int[] results = insertStmt.executeBatch();
            int insertedCount = 0;
            for (int result : results) {
                if (result > 0) insertedCount++;
            }
            
            logger.debug("사육사 배정 완료: %d명 새로 배정됨", insertedCount);
        }
    }

    /**
     * 사육장의 동물들을 조회합니다 (animals.enclosure_id 기반).
     */
    private void loadEnclosureAnimals(Connection connection, Enclosure enclosure) throws SQLException {
        String sql = "SELECT id FROM animals WHERE enclosure_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, enclosure.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String animalId = rs.getString("id");
                    // 실제 Animal 객체는 AnimalManager에서 조회하도록 설계되어 있으므로
                    // 여기서는 ID만 저장
                    enclosure.addInhabitant(animalId, animalId);
                }
            }
        }
    }

    private void loadEnclosureCaretakers(Connection connection, Enclosure enclosure) throws SQLException {
        String sql = "SELECT keeper_id FROM enclosure_caretakers WHERE enclosure_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, enclosure.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String keeperId = rs.getString("keeper_id");
                    // 실제 ZooKeeper 객체는 ZooKeeperManager에서 조회하도록 설계되어 있으므로
                    // 여기서는 ID만 저장
                    enclosure.assignCaretaker(keeperId, keeperId);
                }
            }
        }
    }

    /**
     * 사육장 관련 관계를 정리합니다.
     */
    private void deleteEnclosureRelations(Connection connection, String enclosureId) throws SQLException {
        // 동물들의 enclosure_id를 NULL로 설정 (대기 상태로 변경)
        unassignAnimalsFromEnclosure(connection, enclosureId);

        // 사육사 관계 삭제
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM enclosure_caretakers WHERE enclosure_id = ?")) {
            stmt.setString(1, enclosureId);
            stmt.executeUpdate();
        }
    }

    /**
     * 특정 사육장의 모든 동물들을 대기 상태로 변경합니다.
     */
    private void unassignAnimalsFromEnclosure(Connection connection, String enclosureId) throws SQLException {
        String sql = "UPDATE animals SET enclosure_id = NULL WHERE enclosure_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, enclosureId);
            stmt.executeUpdate();
        }
    }

    private Enclosure mapResultSetToEnclosure(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        Float areaSize = rs.getFloat("area_size");
        Float temperature = rs.getFloat("temperature");
        LocationType locationType = LocationType.valueOf(rs.getString("location_type"));
        EnvironmentType environmentType = EnvironmentType.valueOf(rs.getString("environment_type"));

        return new Enclosure(id, name, areaSize, temperature, locationType, environmentType);
    }
}
