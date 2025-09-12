package app.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.config.DatabaseConnection;
import app.zooKeeper.ZooKeeper;
import app.zooKeeper.zooKeeperEnum.Department;
import app.zooKeeper.zooKeeperEnum.Gender;
import app.zooKeeper.zooKeeperEnum.ZooKeeperConverter;
import app.zooKeeper.zooKeeperEnum.ZooKeeperRank;

public class JdbcZooKeeperRepository {

	private Statement statement;

	private JdbcZooKeeperRepository() {
	}

	private static class SingletonHolder {
		private static final JdbcZooKeeperRepository INSTANCE = new JdbcZooKeeperRepository();
	}

	public static JdbcZooKeeperRepository getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 사육사를 db에 저장하는 메서드입니다.
	 * 
	 * @param id
	 * @param name
	 * @param age
	 * @param genderIndex
	 * @param rankIndex
	 * @param departmentIndex
	 * @param isWorkingIndex
	 * @param experienceYear
	 * @param canHandleDangerAnimalIndex
	 * @param desc
	 */
	public ZooKeeper createZooKeeper(ZooKeeper zk) {
		ZooKeeper newZk = null;
		String sql = """
				INSERT INTO zoo_keepers
				(id, name, age, gender, department, rank_level, is_working, experience_year, can_handle_danger_animal, licenses)
				VALUES (?,?,?,?,?,?,?,?,?,?)
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql);) {
			pstmt.setString(1, zk.getId()); // id
			pstmt.setString(2, zk.getName()); // name
			pstmt.setInt(3, zk.getAge()); // age
			pstmt.setString(4, zk.getGender().name()); // gender
			pstmt.setString(5, zk.getDepartment().name()); // department
			pstmt.setString(6, zk.getRank().name()); // rank
			pstmt.setInt(7, zk.isWorking() ? 1 : 0); // isWorking
			pstmt.setInt(8, zk.getExperienceYear()); // experienceYear
			pstmt.setInt(9, zk.isCanHandleDangerAnimal() ? 1 : 0); // canHandleDangerAnimal
			pstmt.setString(10, listStringToStringMaker(zk.getLicenses())); // desc
			pstmt.executeUpdate();
			newZk = zk;
		} catch (SQLException e) {
			throw new RuntimeException("사육사 등록에 실패하였습니다. " + e.getMessage(), e);
		}
		return newZk;
	}

	/**
	 * 전체사육사를 DB에서 조회하고 리턴하는 메서드 입니다. 
	 * 
	 * @return List<ZooKeeper>
	 */
	public List<ZooKeeper> getZooKeeperListDB() {
		List<ZooKeeper> zooKeepers = new ArrayList<>();
		String sql = """
				SELECT *
				FROM zoo_keepers
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultset = statement.executeQuery(sql);) {
			while (resultset.next()) {
				List<String> list = stringListMaker(resultset);
				ZooKeeper zooKeeper = new ZooKeeper(resultset.getString("id"), resultset.getString("name"),
						resultset.getInt("age"), Gender.valueOf(resultset.getString("gender").toUpperCase()),
						ZooKeeperRank.valueOf(resultset.getString("rank_level").toUpperCase()),
						Department.valueOf(resultset.getString("department").toUpperCase()),
						resultset.getInt("is_working") == 1 ? true : false, resultset.getInt("experience_year"),
						resultset.getInt("can_handle_danger_animal") == 1 ? true : false, list);
				zooKeepers.add(zooKeeper);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zooKeepers;
	}

	/**
	 * ID로 특정 사육사를 찾아 리턴하는 메서드 입니다.
	 * 
	 * @param id
	 * @return ZooKeeper
	 */
	public ZooKeeper getZooKeeperByIdDB(String id) {
		ZooKeeper zk = null;
		String sql = """
				SELECT *
				FROM zoo_keepers
				WHERE id = ?
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql);) {
			pstmt.setString(1, id);
			try (ResultSet resultset = pstmt.executeQuery();) {
				while (resultset.next()) {
					List<String> list = stringListMaker(resultset);
					zk = new ZooKeeper(resultset.getString("id"), resultset.getString("name"), resultset.getInt("age"),
							Gender.valueOf(resultset.getString("gender").toUpperCase()),
							ZooKeeperRank.valueOf(resultset.getString("rank_level").toUpperCase()),
							Department.valueOf(resultset.getString("department").toUpperCase()),
							resultset.getInt("is_working") == 1 ? true : false, resultset.getInt("experience_year"),
							resultset.getInt("can_handle_danger_animal") == 1 ? true : false, list);
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("사육사를 가져오는데 실패했습니다." + e.getMessage(), e);

		}
		return zk;
	}

	/**
	 * Name으로 특정 사육사를 찾아 리턴하는 메서드 입니다.
	 * 
	 * @param name
	 * @return List<ZooKeeper>
	 */

	public List<ZooKeeper> getZooKeeperByNameDB(String name) {
		List<ZooKeeper> zkList = new ArrayList<>();
		String sql = """
				SELECT *
				FROM zoo_keepers
				WHERE name = ?
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, name);
			try (ResultSet resultset = pstmt.executeQuery()) {
				while (resultset.next()) {
					List<String> list = stringListMaker(resultset);
					ZooKeeper zk = new ZooKeeper(resultset.getString("id"), resultset.getString("name"),
							resultset.getInt("age"), Gender.valueOf(resultset.getString("gender").toUpperCase()),
							ZooKeeperRank.valueOf(resultset.getString("rank_level").toUpperCase()),
							Department.valueOf(resultset.getString("department").toUpperCase()),
							resultset.getInt("is_working") == 1 ? true : false, resultset.getInt("experience_year"),
							resultset.getInt("can_handle_danger_animal") == 1 ? true : false, list);
					zkList.add(zk);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("사육사리스트를 가져오는데 실패했습니다." + e.getMessage(), e);
		}
		return zkList;
	}

	/**
	 * Department로 특정 사육사 찾아 리턴하는 메서드 입니다.
	 * 
	 * @param index
	 * @return List<ZooKeeper>
	 */
	public List<ZooKeeper> getZookeeperByDepartmentDB(int index) {
		List<ZooKeeper> zooKeepers = new ArrayList<>();
		String sql = """
				SELECT *
				FROM zoo_keepers
				WHERE department = ?
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
			Department d_enum = ZooKeeperConverter.departmentConverter(index);
			pstmt.setString(1, d_enum.name());
			try (ResultSet resultset = pstmt.executeQuery()) {
				while (resultset.next()) {
					List<String> list = stringListMaker(resultset);
					ZooKeeper zk = new ZooKeeper(resultset.getString("id"), resultset.getString("name"),
							resultset.getInt("age"), Gender.valueOf(resultset.getString("gender").toUpperCase()),
							ZooKeeperRank.valueOf(resultset.getString("rank_level").toUpperCase()),
							Department.valueOf(resultset.getString("department").toUpperCase()),
							resultset.getInt("is_working") == 1 ? true : false, resultset.getInt("experience_year"),
							resultset.getInt("can_handle_danger_animal") == 1 ? true : false, list);
					zooKeepers.add(zk);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("사육사리스트를 가져오는데 실패했습니다." + e.getMessage(), e);
		}
		return zooKeepers;
	}

	/**
	 * 현재 일을 하고 있는 사육사리스트를 반환합니다.
	 * 
	 * @return List<ZooKeepers>
	 */
	public List<ZooKeeper> getWorkingKeepersDB() {
		List<ZooKeeper> zooKeepers = new ArrayList<>();
		String sql = """
				SELECT *
				FROM zoo_keepers
				WHERE is_working = 1
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultset = statement.executeQuery(sql);) {
			while (resultset.next()) {
				List<String> list = stringListMaker(resultset);
				ZooKeeper zooKeeper = new ZooKeeper(resultset.getString("id"), resultset.getString("name"),
						resultset.getInt("age"), Gender.valueOf(resultset.getString("gender").toUpperCase()),
						ZooKeeperRank.valueOf(resultset.getString("rank_level").toUpperCase()),
						Department.valueOf(resultset.getString("department").toUpperCase()),
						resultset.getInt("is_working") == 1 ? true : false, resultset.getInt("experience_year"),
						resultset.getInt("can_handle_danger_animal") == 1 ? true : false, list);
				zooKeepers.add(zooKeeper);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zooKeepers;
	}

	/**
	 * 현재 일을 하고 있는 사육사리스트를 반환합니다.
	 * 
	 * @return boolean
	 */
	public boolean hasWorkingKeepers() {
		String sql = """
				SELECT count(id)
				FROM zoo_keepers
				WHERE is_working = 1
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultset = statement.executeQuery(sql);) {
			if (resultset.next()) {
				int count = resultset.getInt(1);
				return count > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 사육사의 재직현황을 수정하는 매서드입니다.
	 * 
	 * @param targetId
	 * @param index
	 * @return boolean
	 */
	public boolean editIsWorkingDB(String targetId, int index) {
		boolean success = false;
		String sql = """
				UPDATE zoo_keepers
				SET is_working = ?
				WHERE id = ?
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql);) {
			pstmt.setInt(1, index == 1 ? 1 : 0);
			pstmt.setString(2, targetId);
			int rows = pstmt.executeUpdate();
			success = rows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("사육사를 재직현황을 수정했습니다." + e.getMessage(), e);
		}
		return success;
	}

	/** 
	 * 사육사의 위험동물관리여부를 수정할 수 있는 매서드 입니다.
	 * 
	 * @param targetId
	 * @param index
	 * @return boolean
	 */
	public boolean editPermissionDangerAnimalDB(String targetId, int index) {
		boolean success = false;
		String sql = """
				UPDATE zoo_keepers
				SET can_handle_danger_animal = ?
				WHERE id = ?
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, index == 1 ? 1 : 0);
			pstmt.setString(2, targetId);
			int rows = pstmt.executeUpdate();
			success = rows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("위험동물관리여부 수정에 실패했습니다." + e.getMessage(), e);
		}
		return success;
	}

	/**
	 * 사육사를 삭제하는 DB 메서드입니다.
	 * 
	 * @param targetId
	 * @return boolean
	 */
	public boolean deleteZooKeeperDB(String targetId) {
		boolean success = false;
		String sql = """
				DELETE FROM zoo_keepers
				WHERE id = ?
				""";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, targetId);
			int rows = pstmt.executeUpdate();
			success = rows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("사육사 삭제에 실패했습니다." + e.getMessage(), e);
		}
		return success;
	}

	// Util Methods
	/**
	 * 사육사가 매니저급인지 확인하는 메서드 입니다.
	 * 
	 * @param id
	 * @return boolean
	 */
	public boolean checkManager(String id) {
		ZooKeeper zk = getZooKeeperByIdDB(id);
		if (zk.getRank() == ZooKeeperRank.DIRECTOR || zk.getRank() == ZooKeeperRank.MANAGER
				|| zk.getRank() == ZooKeeperRank.HEAD_KEEPER) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * db에 있는 text를 String List로 바꿔주는 헬퍼 메서드입니다.
	 * 
	 * @param resultset
	 * @return List<String>
	 * @throws SQLException
	 */
	private List<String> stringListMaker(ResultSet resultset) throws SQLException {
		String licenses = resultset.getString("licenses");
		List<String> list = licenses != null && !licenses.isEmpty() ? Arrays.asList(licenses.split(","))
				: new ArrayList<>();
		return list;
	}

	private String listStringToStringMaker(List<String> list) {
		if (list == null || list.isEmpty()) {
			return "";
		}
		return String.join(", ", list);
	}

}
