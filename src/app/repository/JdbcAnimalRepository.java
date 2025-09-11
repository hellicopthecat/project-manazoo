package app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import app.animal.Animal;
import app.common.SimpleLogger;
import app.common.ui.MenuUtil;
import app.config.DatabaseConnection;
import app.repository.interfaces.AnimalRepository;
import app.repository.jdbc.JdbcEnclosureRepository;

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

	@Override
	public Animal save(Animal entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Animal> findById(String id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<Animal> findAll() {
		List<Animal> animals = new ArrayList<>();
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);
			String sql = "SELECT * FROM animals";
			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				ResultSet rs = pstmt.executeQuery();

				while (rs.next()) {
					String id = rs.getString(1);
					String name = rs.getString(2);
					String species = rs.getString(3);
					int age = rs.getInt(4);
					String gender = rs.getString(5);
					String healthStatus = rs.getString(6);
					String enclosureId = rs.getString(7);

					Animal animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
					animals.add(animal);
					connection.commit();
				}
				return animals;
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
		}
	}

	@Override
	public Animal update(Animal entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteById(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsById(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

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

	@Override
	public Animal createAnimal(String id, String name, String species, int age, String gender, String healthStatus,
			String enclosureId) {
		String sql = """
				INSERT INTO animals (id, name, species, age, gender, health_status, enclosure_id)
				VALUES (?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, id);
				pstmt.setString(2, name);
				pstmt.setString(3, species);
				pstmt.setInt(4, age);
				pstmt.setString(5, gender);
				pstmt.setString(6, healthStatus);
				pstmt.setString(7, null);

				Animal animal = null;
				if (pstmt.executeUpdate() == 1) {
					animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
					connection.commit();
				} else {
					System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 등록 실패!");
				}
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

	@Override
	public List<Animal> getAnimalList() {
		List<Animal> animals = new ArrayList<>();
		String sql = "SELECT * FROM animals";
		
		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement pstmt = connection.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			
			while (rs.next()) {
				String id = rs.getString(1);
				String name = rs.getString(2);
				String species = rs.getString(3);
				int age = rs.getInt(4);
				String gender = rs.getString(5);
				String healthStatus = rs.getString(6);
				String enclosureId = rs.getString(7);

				Animal animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
				animals.add(animal);
			}
			
			logger.debug("총 {}마리의 동물을 조회했습니다.", animals.size());
			
		} catch (SQLException e) {
			logger.error("동물 목록 조회 중 오류 발생", e);
			// 예외 발생 시에도 빈 리스트 반환 (null 방지)
		}
		
		// 항상 빈 리스트라도 반환하여 null 방지
		return animals;
	}

	@Override
	public Animal getAnimalById(String id) {
		String sql = "SELECT * FROM animals WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String name = rs.getString(2);
					String species = rs.getString(3);
					int age = rs.getInt(4);
					String gender = rs.getString(5);
					String healthStatus = rs.getString(6);
					String enclosureId = rs.getString(7);

					return new Animal(id, name, species, age, gender, healthStatus, enclosureId);
				} else {
					return null;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("동물 조회 중 오류 발생 (ID: " + id + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<Animal> getAnimalsByName(String name) {
		if (name == null) {
			return new ArrayList<>();
		}
		
		List<Animal> animals = new ArrayList<>();
		String sql = "SELECT * FROM animals WHERE LOWER(name) = LOWER(?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, name);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String id = rs.getString(1);
					String animalName = rs.getString(2);
					String species = rs.getString(3);
					int age = rs.getInt(4);
					String gender = rs.getString(5);
					String healthStatus = rs.getString(6);
					String enclosureId = rs.getString(7);

					Animal animal = new Animal(id, animalName, species, age, gender, healthStatus, enclosureId);
					animals.add(animal);
				}
			}
			
			logger.debug("이름이 '{}'인 동물 {}마리를 조회했습니다.", name, animals.size());
			
		} catch (SQLException e) {
			logger.error("이름으로 동물 조회 중 오류 발생: " + name, e);
			// 예외 발생 시에도 빈 리스트 반환
		}
		
		return animals;
	}

	@Override
	public List<Animal> getAnimalsBySpecies(String species) {
		if (species == null) {
			return new ArrayList<>();
		}
		
		List<Animal> animals = new ArrayList<>();
		String sql = "SELECT * FROM animals WHERE species = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, species);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String id = rs.getString(1);
					String name = rs.getString(2);
					String animalSpecies = rs.getString(3);
					int age = rs.getInt(4);
					String gender = rs.getString(5);
					String healthStatus = rs.getString(6);
					String enclosureId = rs.getString(7);

					Animal animal = new Animal(id, name, animalSpecies, age, gender, healthStatus, enclosureId);
					animals.add(animal);
				}
			}
			
			logger.debug("종류가 '{}'인 동물 {}마리를 조회했습니다.", species, animals.size());
			
		} catch (SQLException e) {
			logger.error("종별 동물 조회 중 오류 발생: " + species, e);
			// 예외 발생 시에도 빈 리스트 반환
		}
		
		return animals;
	}

	@Override
	public Animal updateAnimal(String animalId, Animal animal) {
		// animalId 가 숫자로 들어온 경우 ==> 동물의 나이를 수정
		if (isNumeric(animalId)) {
			int age = Integer.parseInt(animalId);
			String sql = "UPDATE animals SET age = ? WHERE id = ?";

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

			// animalId 가 String으로 들어온 경우 ==> 동물의 건강상태를 수정
		} else {
			String sql = "UPDATE animals SET health_status = ? WHERE id = ?";

			try (Connection connection = DatabaseConnection.getConnection()) {
				connection.setAutoCommit(false);

				try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
					pstmt.setString(1, animalId);
					pstmt.setString(2, animal.getId());

					if (pstmt.executeUpdate() == 1) {
						animal.setHealthStatus(animalId);
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
	}

	@Override
	public boolean removeAnimal(String animalId) {
		if (animalId == null) {
			return false;
		}

		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);

			try {
				String sql = "DELETE FROM animals WHERE id = ?";

				try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
					pstmt.setString(1, animalId);
					int affected = pstmt.executeUpdate();

					connection.commit();
					return affected > 0;
				}
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

	@Override
	public boolean hasAvailableAnimals() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, Animal> getAvailableAnimals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Animal> getWorkingCopyOfAvailableAnimals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Animal> getAnimalFromAll(String animalId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Animal removeAvailableAnimal(String animalId, String enclosureId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Animal releaseAnimalFromEnclosure(String animalId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAnimalInEnclosure(String animalId, String enclosureId) {
		// TODO Auto-generated method stub
		return false;
	}

	// =================== 내부 헬퍼 메소드 ===================

	// 입력받은 String 이 Int 로 변환 가능한지를 true/false 로 반환하는 메소드
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

}
