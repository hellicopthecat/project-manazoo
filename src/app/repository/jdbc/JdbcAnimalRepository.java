package app.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import app.animal.Animal;
import app.common.SimpleLogger;
import app.common.ui.MenuUtil;
import app.config.DatabaseConnection;
import app.repository.interfaces.AnimalRepository;

public class JdbcAnimalRepository implements AnimalRepository {

	// 로거 인스턴스
	private static final SimpleLogger logger = SimpleLogger.getLogger(JdbcEnclosureRepository.class);

	// ==================== Singleton 패턴 구현 ====================

	private static class SingletonHolder {
		private static final JdbcAnimalRepository INSTANCE = new JdbcAnimalRepository();
	}

	/**
	 * JdbcEnclosureRepository의 싱글톤 인스턴스를 반환합니다.
	 * 
	 * <p>
	 * <strong>Singleton 패턴 사용 이유:</strong>
	 * </p>
	 * <ul>
	 * <li>메모리 효율성: 하나의 Repository 인스턴스만 생성</li>
	 * <li>일관성: 모든 사육장 관련 작업이 동일한 인스턴스를 통해 처리</li>
	 * <li>스레드 안전성: Lazy 초기화로 멀티스레드 환경에서 안전</li>
	 * </ul>
	 * 
	 * @return JdbcEnclosureRepository 싱글톤 인스턴스
	 */
	public static JdbcAnimalRepository getInstance() {
		logger.debug("JdbcAnimalRepository 싱글톤 인스턴스 반환");
		return SingletonHolder.INSTANCE;
	}

	private JdbcAnimalRepository() {
		// private 생성자로 외부 인스턴스 생성 방지
	}

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

		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, animal.getId());
	            pstmt.setString(2, animal.getName());
	            pstmt.setString(3, animal.getSpecies());
	            pstmt.setInt(4, animal.getAge());
	            pstmt.setString(5, animal.getGender());
	            pstmt.setString(6, animal.getHealthStatus());
	            pstmt.setString(7, null);

				if (pstmt.executeUpdate() == 0) {
					System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 등록 실패!");
				}
				connection.commit();
				return animal;
			} catch (Exception e) {
				connection.rollback();
				throw new RuntimeException("동물 저장 중 오류 발생: " + e.getMessage(), e);
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
		}
	}

	/**
	 * ID를 통해 동물을 조회합니다.
	 * 
	 * @param animalId 조회할 동물의 고유 식별자
	 * @return 조회된 동물 객체 (Optional로 래핑됨)
	 */
	@Override
	public Optional<Animal> findById(String animalId) {
		if (animalId == null) {
            return Optional.empty();
        }
		
		String sql = """
	            SELECT id, name, species, age, gender, health_status, enclosure_id
	            FROM animals 
	            WHERE id = ?
	            """;

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {

			pstmt.setString(1, animalId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Animal animal = mapResultSetToAnimal(rs);
					return Optional.ofNullable(animal);
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("동물 조회 중 오류 발생 (ID: " + animalId + "): " + e.getMessage(), e);
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
		
		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement pstmt = connection.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					Animal animal = mapResultSetToAnimal(rs);
					animals.add(animal);
				}
				return animals;
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
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
            throw new IllegalArgumentException(MenuUtil.DEFAULT_PREFIX + "수정하려는 동물이 존재하지 않습니다: " + animal.getId());
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
            throw new RuntimeException("동물 수정 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
	}
	
    /**
     * 기존 동물의 나이를 업데이트합니다.
     * 
     * @param animal 업데이트할 동물의 나이 
     * @param animal 업데이트할 동물 객체
     * @return 업데이트된 동물 객체
     */
	public Animal update(int age, Animal animal) { 
        Objects.requireNonNull(animal, "동물 객체는 null일 수 없습니다.");
        Objects.requireNonNull(animal.getId(), "동물 ID는 null일 수 없습니다.");
        
        if (!existsById(animal.getId())) {
            throw new IllegalArgumentException(MenuUtil.DEFAULT_PREFIX + "수정하려는 동물이 존재하지 않습니다: " + animal.getId());
        }
		
		String sql = """
			UPDATE animals 
			SET age = ? 
			WHERE id = ?
			""";

		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setInt(1, age);
				pstmt.setString(2, animal.getId());

				if (pstmt.executeUpdate() == 1) {
					animal.setAge(age);
					connection.commit();
				} else {
					System.out.print(MenuUtil.DEFAULT_PREFIX + "수정이 실패했습니다.");
				}
				return animal;
			} catch (Exception e) {
				connection.rollback();
				throw new RuntimeException("동물 수정 중 오류 발생: " + e.getMessage(), e);
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
		}
	}
	
    /**
     * 기존 동물의 건강상태를 업데이트합니다.
     * 
     * @param animal 업데이트할 동물의 건강상태 
     * @param animal 업데이트할 동물 객체
     * @return 업데이트된 동물 객체
     */
	public Animal update(String healthStatus, Animal animal) {
		Objects.requireNonNull(animal, "동물 객체는 null일 수 없습니다.");
        Objects.requireNonNull(animal.getId(), "동물 ID는 null일 수 없습니다.");
        
        if (!existsById(animal.getId())) {
            throw new IllegalArgumentException(MenuUtil.DEFAULT_PREFIX + "수정하려는 동물이 존재하지 않습니다: " + animal.getId());
        }
		
		String sql = """
			UPDATE animals 
			SET health_status = ? 
			WHERE id = ?
			""";

		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, healthStatus);
				pstmt.setString(2, animal.getId());

				if (pstmt.executeUpdate() == 1) {
					animal.setHealthStatus(healthStatus);
					connection.commit();
				} else {
					System.out.print(MenuUtil.DEFAULT_PREFIX + "수정이 실패했습니다.");
				}
				return animal;
			} catch (Exception e) {
				connection.rollback();
				throw new RuntimeException("동물 수정 중 오류 발생: " + e.getMessage(), e);
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
		}
		
	}
	
    /**
     * ID를 통해 동물을 삭제합니다.
     * 
     * @param id 삭제할 동물의 고유 식별자
     * @return 삭제 성공 여부
     */
	@Override
	public boolean deleteById(String animalId) {
		if (animalId == null) {
			return false;
		}

		String sql = "DELETE FROM animals WHERE id = ?";
		
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, animalId);
				
				int affected = pstmt.executeUpdate();
				connection.commit();
				return affected > 0;
				
			} catch (Exception e) {
				connection.rollback();
				throw new RuntimeException("동물 삭제 중 오류 발생: " + e.getMessage(), e);
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new RuntimeException("동물 삭제 중 데이터베이스 연결 실패: " + e.getMessage(), e);
		}
	}

    /**
     * 특정 ID의 동물이 존재하는지 확인합니다.
     * 
     * @param id 확인할 동물의 고유 식별자
     * @return 존재 여부
     */
	@Override
	public boolean existsById(String animalId) {
		if (animalId == null) {
            return false;
        }
        
        String sql = """
	            SELECT id, name, species, age, gender, health_status, enclosure_id
	            FROM animals 
	            WHERE id = ?
	            """;
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, animalId);
            
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
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            int deletedCount = pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("동물 삭제 중 오류 발생: " + e.getMessage(), e);
        }
	}

	/**
	 * 저장소에 있는 동물의 총 마리수를 반환합니다.
	 * 
	 * @return 동물 마리수
	 */
	@Override
	public long count() {
		String sql = "SELECT COUNT(*) FROM animals";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			if (rs.next()) {
				return rs.getLong(1);
			}

		} catch (SQLException e) {
			throw new RuntimeException("동물 마리수 조회 중 오류 발생: " + e.getMessage(), e);
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
	public Animal createAnimal(String id, String name, String species, int age, String gender, String healthStatus,
			String enclosureId) {
		Animal animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
		return animal;
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
	public Animal getAnimalById(String animalId) {
        return findById(animalId).orElse(null);
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
	            WHERE LOWER(name) = LOWER(?)
	            """;
		name = name.toLowerCase();
		
		List<Animal> animals = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement pstmt = connection.prepareStatement(sql)) {

			pstmt.setString(1, name);
			Animal animal = null;

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String id = rs.getString(1);
					String species = rs.getString(3);
					int age = rs.getInt(4);
					String gender = rs.getString(5);
					String healthStatus = rs.getString(6);
					String enclosureId = rs.getString(7);

					animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
					animals.add(animal);
					return animals;
				} else {
					return new ArrayList<>();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("동물 조회 중 오류 발생 (Name: " + name + "): " + e.getMessage(), e);
		}
	}

    /**
     * 특정 종의 동물들을 조회합니다.
     * 
     * @param species 동물 종류
     * @return 해당 종의 동물 리스트 
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

		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, species);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Animal animal = mapResultSetToAnimal(rs);
					animals.add(animal);
				}
				return animals;
			}
		} catch (SQLException e) {
			throw new RuntimeException("동물 조회 중 오류 발생 (Species: " + species + "): " + e.getMessage(), e);
		}
	}

    /**
     * 특정 동물의 나이와 건강상태를 업데이트합니다. 
     * change값에 Int가 입력되면 나이를 업데이트, String이 입력되면 건강상태를 업데이트합니다.
     * 
     * @param change	수정할 정보 (나이 or 건강상태)
     * @param animal	수정할 동물 객체
     * @return 수정된 동물 객체
     */
	@Override
	public Animal updateAnimal(String change, Animal animal) { 
		if (!existsById(animal.getId())) {
            throw new IllegalArgumentException("수정하려는 동물이 존재하지 않습니다: " + animal.getId());
        }
        
 		if (isNumeric(change)) {
 			int age = Integer.parseInt(change);
 			return update(age, animal);
 			
 		} else {
 			String healthStatus = change;
 			return update(healthStatus, animal);
 		}
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
     * 입력값 String이 Int로 변환 가능한 여부를 체크합니다.
     * 
     * @param str String 입력값 
     * @return Int로 변환 가능하면 true
     */
	private static boolean isNumeric(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
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
