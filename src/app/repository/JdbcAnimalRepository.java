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
import app.enclosure.Enclosure;
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
				pstmt.close();
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
			throw new RuntimeException("사육장 개수 조회 중 오류 발생: " + e.getMessage(), e);
		}

		return 0;
	}

	@Override
	public Animal createAnimal(String id, String name, String species, int age, String gender, String healthStatus,
			String enclosureId) {
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);
			String sql = """
					INSERT INTO animals (id, name, species, age, gender, health_status, enclosure_id)
					VALUES (?, ?, ?, ?, ?, ?, ?)
					""";

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
				pstmt.close();
				return animal;
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Animal> getAnimalList() {
		return null;
	}

	@Override
	public Animal getAnimalById(String id) {
		String sql = "SELECT * FROM animals WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, id);
			Animal animal = null;

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String name = rs.getString(2);
					String species = rs.getString(3);
					int age = rs.getInt(4);
					String gender = rs.getString(5);
					String healthStatus = rs.getString(6);
					String enclosureId = rs.getString(7);

					animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
				}
				stmt.close();
				return animal;
			}
		}

	}catch(

	SQLException e)
	{
		throw new RuntimeException("동물 조회 중 오류 발생 (ID: " + id + "): " + e.getMessage(), e);
	}
	}

	@Override
	public List<Animal> getAnimalsByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public static List<Animal> getAnimalsByName(Connection connection, String name) throws SQLException {
		List<Animal> animals = new ArrayList<>();

		String sql = "select * from animals where name = ?";
		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, name);
		ResultSet rs = pstmt.executeQuery();

		Animal animal = null;

		if (rs.next()) {
			String id = rs.getString(1);
			String species = rs.getString(3);
			int age = rs.getInt(4);
			String gender = rs.getString(5);
			String healthStatus = rs.getString(6);
			String enclosureId = rs.getString(7);

			animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
			animals.add(animal);
		}
		pstmt.close();
		return animals;
	}

	@Override
	public List<Animal> getAnimalsBySpecies(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	public static List<Animal> getAnimalsBySpecies(Connection connection, String species) throws SQLException {
		List<Animal> animals = new ArrayList<>();

		String sql = "select * from animals where species = ?";
		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, species);
		ResultSet rs = pstmt.executeQuery();

		Animal animal = null;

		if (rs.next()) {
			String id = rs.getString(1);
			String name = rs.getString(2);
			int age = rs.getInt(4);
			String gender = rs.getString(5);
			String healthStatus = rs.getString(6);
			String enclosureId = rs.getString(7);

			animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
			animals.add(animal);
		}
		pstmt.close();
		return animals;
	}

	@Override
	public Animal updateAnimal(String animalId, Animal animal) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Animal updateAnimal(Connection connection, Animal animal, int age) throws SQLException {
		String sql = "update animals set age = ? where id = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setInt(1, age);
		pstmt.setString(2, animal.getId());

		if (pstmt.executeUpdate() == 1) {
			animal.setAge(age);
		} else {
			System.out.print(MenuUtil.DEFAULT_PREFIX + "수정이 실패했습니다.");
		}
		pstmt.close();
		return animal;
	}

	public static Animal updateAnimal(Connection connection, Animal animal, String healthStatus) throws SQLException {
		String sql = "update animals set health_status = ? where id = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, healthStatus);
		pstmt.setString(2, animal.getId());

		if (pstmt.executeUpdate() == 1) {
			animal.setHealthStatus(healthStatus);
		} else {
			System.out.print(MenuUtil.DEFAULT_PREFIX + "수정이 실패했습니다.");
		}
		pstmt.close();
		return animal;
	}

	@Override
	public boolean removeAnimal(String animalId) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void removeAnimal(Connection connection, String animalId) throws SQLException {
		String sql = "delete from animals where id = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, animalId);

		if (pstmt.executeUpdate() == 1) {

		} else {

		}

		pstmt.close();
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

}
