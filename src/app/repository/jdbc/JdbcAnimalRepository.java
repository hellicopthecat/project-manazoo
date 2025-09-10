package app.repository.jdbc;

import java.sql.*;
import java.util.*;

import app.animal.Animal;
import app.repository.interfaces.AnimalRepository;
import app.config.DatabaseConnection;
import app.common.SimpleLogger;

/**
 * JDBC 기반 동물 Repository 구현체입니다.
 * MemoryAnimalRepository와 동일한 기능을 데이터베이스 기반으로 제공합니다.
 * Singleton 패턴을 적용하여 애플리케이션 전체에서 단일 데이터 저장소를 사용합니다.
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>데이터베이스 기반 데이터 저장</li>
 *   <li>동물 배치 관리 기능</li>
 *   <li>타입 안전성 확보</li>
 *   <li>기존 AnimalManager와의 완전 호환</li>
 *   <li>Singleton 패턴으로 데이터 일관성 보장</li>
 * </ul>
 * 
 * @author ManazooTeam
 * @version 1.0
 * @since 2025-09-09
 * @see AnimalRepository
 * @see Animal
 */
public class JdbcAnimalRepository implements AnimalRepository {
    
    // 로거 인스턴스
    private static final SimpleLogger logger = SimpleLogger.getLogger(JdbcAnimalRepository.class);
    
    /**
     * private 생성자 - Singleton 패턴 적용
     */
    private JdbcAnimalRepository() {
        // private 생성자로 외부 인스턴스 생성 방지
    }
    
    /**
     * Initialization-on-demand holder pattern을 사용한 Thread-safe Singleton
     * JVM의 클래스 로딩 메커니즘을 활용하여 동기화 오버헤드 없이 lazy loading 구현
     */
    private static class SingletonHolder {
        private static final JdbcAnimalRepository INSTANCE = new JdbcAnimalRepository();
    }
    
    /**
     * Singleton 인스턴스를 반환합니다.
     * Holder Pattern을 사용하여 최적의 성능과 Thread Safety를 보장합니다.
     * 
     * @return JdbcAnimalRepository 인스턴스
     */
    public static JdbcAnimalRepository getInstance() {
        logger.debug("JdbcAnimalRepository 싱글톤 인스턴스 반환");
        return SingletonHolder.INSTANCE;
    }
    
    // =================================================================
    // Repository<Animal, String> 기본 CRUD 구현
    // =================================================================
    
    /**
     * 동물 객체를 저장소에 저장합니다.
     * 
     * @param animal 저장할 동물 객체
     * @return 저장된 동물 객체
     * @throws NullPointerException 동물 객체나 ID가 null인 경우
     */
    @Override
    public Animal save(Animal animal) {
        Objects.requireNonNull(animal, "동물 객체는 null일 수 없습니다.");
        Objects.requireNonNull(animal.getId(), "동물 ID는 null일 수 없습니다.");
        
        String sql = """
            INSERT INTO animals (id, name, species, age, gender, health_status, enclosure_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, animal.getId());
            pstmt.setString(2, animal.getName());
            pstmt.setString(3, animal.getSpecies());
            pstmt.setInt(4, animal.getAge());
            pstmt.setString(5, animal.getGender());
            pstmt.setString(6, animal.getHealthStatus());
            pstmt.setString(7, animal.getEnclosureId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("동물 저장에 실패했습니다.");
            }
            
            logger.debug("동물 저장 완료: ID=%s", animal.getId());
            return animal;
            
        } catch (SQLException e) {
            logger.error("동물 저장 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 저장 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * ID를 통해 동물을 조회합니다.
     * 
     * @param id 조회할 동물의 고유 식별자
     * @return 조회된 동물 객체 (Optional로 래핑됨)
     */
    @Override
    public Optional<Animal> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        
        String sql = """
            SELECT id, name, species, age, gender, health_status, enclosure_id
            FROM animals 
            WHERE id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Animal animal = mapResultSetToAnimal(rs);
                    logger.debug("동물 조회 성공: ID=%s", id);
                    return Optional.of(animal);
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 저장소에 있는 모든 동물을 조회합니다.
     * 
     * @return 모든 동물의 리스트
     */
    @Override
    public List<Animal> findAll() {
        List<Animal> animals = new ArrayList<>();
        
        String sql = """
            SELECT id, name, species, age, gender, health_status, enclosure_id
            FROM animals 
            ORDER BY created_at DESC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                animals.add(mapResultSetToAnimal(rs));
            }
            
            logger.debug("전체 동물 조회 완료: %d마리", animals.size());
            return animals;
            
        } catch (SQLException e) {
            logger.error("전체 동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("전체 동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 기존 동물의 정보를 업데이트합니다.
     * 
     * @param animal 업데이트할 동물 객체
     * @return 업데이트된 동물 객체
     * @throws IllegalArgumentException 수정하려는 동물이 존재하지 않는 경우
     * @throws NullPointerException 동물 객체나 ID가 null인 경우
     */
    @Override
    public Animal update(Animal animal) {
        Objects.requireNonNull(animal, "동물 객체는 null일 수 없습니다.");
        Objects.requireNonNull(animal.getId(), "동물 ID는 null일 수 없습니다.");
        
        if (!existsById(animal.getId())) {
            throw new IllegalArgumentException("수정하려는 동물이 존재하지 않습니다: " + animal.getId());
        }
        
        String sql = """
            UPDATE animals 
            SET name = ?, species = ?, age = ?, gender = ?, health_status = ?, enclosure_id = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, animal.getName());
            pstmt.setString(2, animal.getSpecies());
            pstmt.setInt(3, animal.getAge());
            pstmt.setString(4, animal.getGender());
            pstmt.setString(5, animal.getHealthStatus());
            pstmt.setString(6, animal.getEnclosureId());
            pstmt.setString(7, animal.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("동물 수정에 실패했습니다.");
            }
            
            logger.debug("동물 수정 완료: ID=%s", animal.getId());
            return animal;
            
        } catch (SQLException e) {
            logger.error("동물 수정 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 수정 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * ID를 통해 동물을 삭제합니다.
     * 
     * @param id 삭제할 동물의 고유 식별자
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        
        String sql = "DELETE FROM animals WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.debug("동물 삭제 완료: ID=%s", id);
            } else {
                logger.debug("삭제할 동물을 찾을 수 없음: ID=%s", id);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("동물 삭제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 삭제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 특정 ID의 동물이 존재하는지 확인합니다.
     * 
     * @param id 확인할 동물의 고유 식별자
     * @return 존재 여부
     */
    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        
        String sql = "SELECT 1 FROM animals WHERE id = ? LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("동물 존재 확인 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 존재 확인 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 저장소의 모든 동물을 삭제합니다.
     */
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM animals";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int deletedCount = pstmt.executeUpdate();
            logger.debug("전체 동물 삭제 완료: %d마리 삭제", deletedCount);
            
        } catch (SQLException e) {
            logger.error("전체 동물 삭제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("전체 동물 삭제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 저장소에 있는 동물의 총 개수를 반환합니다.
     * 
     * @return 동물 개수
     */
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM animals";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                long count = rs.getLong(1);
                logger.debug("동물 개수 조회: %d마리", count);
                return count;
            }
            
        } catch (SQLException e) {
            logger.error("동물 개수 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 개수 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    // =================================================================
    // AnimalRepository 특화 메서드 구현
    // =================================================================
    
    /**
     * 새로운 동물을 생성하여 저장소에 추가합니다.
     * 
     * @param id           동물 ID
     * @param name         동물 이름
     * @param species      종류
     * @param age          나이
     * @param gender       성별
     * @param healthStatus 건강 상태
     * @param enclosureId  사육장 ID
     * @return 생성된 동물 객체
     */
    @Override
    public Animal createAnimal(String id, String name, String species, int age, String gender, 
                              String healthStatus, String enclosureId) {
        
        logger.debug("동물 생성: ID=%s, 이름=%s, 종=%s", id, name, species);
        Animal animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
        return save(animal);
    }
    
    /**
     * 저장소의 모든 동물 목록을 반환합니다.
     * 
     * @return 전체 동물 목록
     */
    @Override
    public List<Animal> getAnimalList() {
        return findAll();
    }
    
    /**
     * 특정 ID의 동물을 조회합니다.
     * 
     * @param id 동물 ID
     * @return 조회된 동물 객체 (없으면 null)
     */
    @Override
    public Animal getAnimalById(String id) {
        return findById(id).orElse(null);
    }
    
    /**
     * 특정 이름을 가진 동물들을 조회합니다.
     * 
     * @param name 동물 이름
     * @return 해당 이름의 동물 목록
     */
    @Override
    public List<Animal> getAnimalsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = """
            SELECT id, name, species, age, gender, health_status, enclosure_id
            FROM animals 
            WHERE name = ?
            """;
        
        List<Animal> animals = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    animals.add(mapResultSetToAnimal(rs));
                }
            }
            
            logger.debug("이름별 동물 조회 완료: 이름=%s, 결과=%d마리", name, animals.size());
            return animals;
            
        } catch (SQLException e) {
            logger.error("이름별 동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 특정 종의 동물들을 조회합니다.
     * 
     * @param species 동물 종류
     * @return 해당 종의 동물 목록
     */
    @Override
    public List<Animal> getAnimalsBySpecies(String species) {
        if (species == null || species.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = """
            SELECT id, name, species, age, gender, health_status, enclosure_id
            FROM animals 
            WHERE species = ?
            """;
        
        List<Animal> animals = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, species.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    animals.add(mapResultSetToAnimal(rs));
                }
            }
            
            logger.debug("종별 동물 조회 완료: 종=%s, 결과=%d마리", species, animals.size());
            return animals;
            
        } catch (SQLException e) {
            logger.error("종별 동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 특정 동물의 정보를 업데이트합니다.
     * 
     * @param animalId 수정할 동물 ID
     * @param animal   수정된 동물 객체
     * @return 수정된 동물 객체
     * @throws IllegalArgumentException 수정하려는 동물이 존재하지 않는 경우
     */
    @Override
    public Animal updateAnimal(String animalId, Animal animal) {
        if (!existsById(animalId)) {
            throw new IllegalArgumentException("수정하려는 동물이 존재하지 않습니다: " + animalId);
        }
        
        return update(animal);
    }
    
    /**
     * 특정 ID의 동물을 삭제합니다.
     * 
     * @param animalId 삭제할 동물 ID
     * @return 삭제 성공 여부
     */
    @Override
    public boolean removeAnimal(String animalId) {
        return deleteById(animalId);
    }
    
    // =================================================================
    // 사육장 배치 관리 메서드
    // =================================================================
    
    /**
     * 배치 가능한 동물(사육장이 미배정된 동물)이 있는지 확인합니다.
     * enclosureId가 null이거나 빈 문자열인 동물이 배치 가능한 동물로 간주됩니다.
     * 
     * @return 배치 가능한 동물 존재 여부
     */
    @Override
    public boolean hasAvailableAnimals() {
        String sql = "SELECT 1 FROM animals WHERE enclosure_id IS NULL OR enclosure_id = '' LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            return rs.next();
            
        } catch (SQLException e) {
            logger.error("배치 가능한 동물 확인 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("배치 가능한 동물 확인 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 배치 가능한 동물들의 목록을 반환합니다.
     * enclosureId가 null이거나 빈 문자열인 동물들을 필터링하여 반환합니다.
     * 
     * @return 배치 가능한 동물들의 Map (Key: Animal ID, Value: Animal 객체)
     */
    @Override
    public Map<String, Animal> getAvailableAnimals() {
        String sql = """
            SELECT id, name, species, age, gender, health_status, enclosure_id
            FROM animals 
            WHERE enclosure_id IS NULL OR enclosure_id = ''
            ORDER BY name
            """;
        
        Map<String, Animal> availableAnimals = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Animal animal = mapResultSetToAnimal(rs);
                availableAnimals.put(animal.getId(), animal);
            }
            
            logger.debug("배치 가능한 동물 조회 완료: %d마리", availableAnimals.size());
            return availableAnimals;
            
        } catch (SQLException e) {
            logger.error("배치 가능한 동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("배치 가능한 동물 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 배치 가능한 동물들의 작업용 복사본을 반환합니다.
     * Working Data Pattern을 적용하여 원본 데이터를 수정하지 않고 작업할 수 있도록 합니다.
     * 
     * @return 배치 가능한 동물들의 복사본 Map
     */
    @Override
    public Map<String, Animal> getWorkingCopyOfAvailableAnimals() {
        Map<String, Animal> availableAnimals = getAvailableAnimals();
        return new HashMap<>(availableAnimals);
    }
    
    /**
     * 전체 동물 목록에서 특정 ID의 동물을 검색합니다.
     * 
     * @param animalId 검색할 동물 ID
     * @return Optional<Animal> 검색된 동물 (없으면 empty)
     */
    @Override
    public Optional<Animal> getAnimalFromAll(String animalId) {
        return findById(animalId);
    }
    
    /**
     * 배치 가능한 상태의 동물을 특정 사육장에 배치합니다.
     * 동물의 enclosureId를 설정하여 배치된 상태로 변경합니다.
     * 
     * @param animalId    배치할 동물 ID
     * @param enclosureId 배치할 사육장 ID
     * @return 배치된 동물 객체 (배치 불가능하면 null)
     */
    @Override
    public Animal removeAvailableAnimal(String animalId, String enclosureId) {
        if (animalId == null || enclosureId == null) {
            return null;
        }
        
        String sql = "UPDATE animals SET enclosure_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND (enclosure_id IS NULL OR enclosure_id = '')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, enclosureId);
            pstmt.setString(2, animalId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.debug("동물 사육장 배정 완료: 동물ID=%s, 사육장ID=%s", animalId, enclosureId);
                return getAnimalById(animalId);
            }
            
            return null;
            
        } catch (SQLException e) {
            logger.error("동물 사육장 배정 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 사육장 배정 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 동물을 사육장에서 해제하여 다시 배치 가능한 상태로 만듭니다.
     * 동물의 enclosureId를 null로 설정합니다.
     * 
     * @param animalId 해제할 동물 ID
     * @return 해제된 동물 객체 (동물이 없으면 null)
     */
    @Override
    public Animal releaseAnimalFromEnclosure(String animalId) {
        if (animalId == null) {
            return null;
        }
        
        String sql = "UPDATE animals SET enclosure_id = NULL, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, animalId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.debug("동물 사육장 해제 완료: 동물ID=%s", animalId);
                return getAnimalById(animalId);
            }
            
            return null;
            
        } catch (SQLException e) {
            logger.error("동물 사육장 해제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 사육장 해제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 동물이 특정 사육장에 배치되어 있는지 확인합니다.
     * 
     * @param animalId    확인할 동물 ID
     * @param enclosureId 확인할 사육장 ID
     * @return 해당 사육장에 배치되어 있으면 true
     */
    @Override
    public boolean isAnimalInEnclosure(String animalId, String enclosureId) {
        if (animalId == null || enclosureId == null) {
            return false;
        }
        
        String sql = "SELECT 1 FROM animals WHERE id = ? AND enclosure_id = ? LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, animalId);
            pstmt.setString(2, enclosureId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("동물 사육장 배정 확인 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
            throw new RuntimeException("동물 사육장 배정 확인 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    // =================================================================
    // 헬퍼 메서드
    // =================================================================
    
    /**
     * ResultSet을 Animal 객체로 매핑합니다.
     * 
     * @param rs 데이터베이스 조회 결과
     * @return 매핑된 Animal 객체
     * @throws SQLException SQL 처리 오류 시
     */
    private Animal mapResultSetToAnimal(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String species = rs.getString("species");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String healthStatus = rs.getString("health_status");
        String enclosureId = rs.getString("enclosure_id");
        
        return new Animal(id, name, species, age, gender, healthStatus, enclosureId);
    }
    
    /**
     * Repository의 문자열 표현을 반환합니다.
     * 현재 저장된 동물의 개수 정보를 포함합니다.
     * 
     * @return Repository 정보 문자열
     */
    @Override
    public String toString() {
        try {
            return String.format("JdbcAnimalRepository{동물수=%d}", count());
        } catch (Exception e) {
            return "JdbcAnimalRepository{connection=error}";
        }
    }
}
