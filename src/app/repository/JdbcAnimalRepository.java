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
import app.common.ui.MenuUtil;
import app.repository.interfaces.AnimalRepository;

public class JdbcAnimalRepository implements AnimalRepository {

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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0;
	}

	public static int count(Connection connection) throws SQLException {
		String sql = "select count(*) from animals";
		try (PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		}
	}

	@Override
	public Animal createAnimal(String id, String name, String species, int age, String gender, String healthStatus,
			String enclosureId) {
		Animal animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
		return save(animal);
	}

	public static Animal createAnimal(Connection connection, String id, String name, String species, int age,
			String gender, String healthStatus) throws SQLException {
		String sql = "insert into animals (id, name, species, age, gender, health_status, enclosure_id, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, id);
		pstmt.setString(2, name);
		pstmt.setString(3, species);
		pstmt.setInt(4, age);
		pstmt.setString(5, gender);
		pstmt.setString(6, healthStatus);
		pstmt.setString(7, null);
		pstmt.setString(8, null);
		pstmt.setString(9, null);

		Animal animal = null;
		if (pstmt.executeUpdate() == 1) {
			animal = new Animal(id, name, species, age, gender, healthStatus, null);
		} else {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 등록 실패!");
		}
		pstmt.close();
		return animal;
	}

	@Override
	public List<Animal> getAnimalList() {
		return null;
	}

	public static List<Animal> getAnimalList(Connection connection) throws SQLException {
		List<Animal> animals = new ArrayList<>();

		String sql = "select * from animals";
		PreparedStatement pstmt = connection.prepareStatement(sql);
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
		}
		pstmt.close();
		return animals;
	}

	@Override
	public Animal getAnimalById(String id) {
		return null;
	}

	public static Animal getAnimalById(Connection connection, String id) throws SQLException {
		String sql = "select * from animals where id = ?";
		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, id);
		ResultSet rs = pstmt.executeQuery();

		Animal animal = null;

		if (rs.next()) {
			String name = rs.getString(2);
			String species = rs.getString(3);
			int age = rs.getInt(4);
			String gender = rs.getString(5);
			String healthStatus = rs.getString(6);
			String enclosureId = rs.getString(7);

			animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
		}
		pstmt.close();
		return animal;
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

	// ???
//	public static Animal updateAnimal(Connection connection, Animal animal, String enclosureId) throws SQLException {
//		String sql = "update animals set health_status = ? where id = ?";
//		
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		pstmt.setString(1, health_status);
//		pstmt.setString(2, animal.getId());
//		
//		if (pstmt.executeUpdate() == 1) {
//			animal.setHealthStatus(health_status);
//		} else {
//			System.out.print(MenuUtil.DEFAULT_PREFIX + "수정이 실패했습니다.");
//		}
//		pstmt.close();
//		return animal;
//	}

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
