package app.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.zooKeeper.ZooKeeper;
import app.zooKeeper.zooKeeperEnum.Department;
import app.zooKeeper.zooKeeperEnum.Gender;
import app.zooKeeper.zooKeeperEnum.ZooKeeperConverter;
import app.zooKeeper.zooKeeperEnum.ZooKeeperRank;

public class JdbcZooKeeperRepository {

	private Statement statement;
	private final String URL = "jdbc:mysql://127.0.0.1:3306/manazoo";
	private final String USER = "root";
	private final String PW = "1111";

	private JdbcZooKeeperRepository() {
	}

	private static class SingletonHolder {
		private static final JdbcZooKeeperRepository INSTANCE = new JdbcZooKeeperRepository();
	}

	public static JdbcZooKeeperRepository getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**a
	 * 사육사 DB에 연결하는 메서드 입니다.
	 */
	private Connection connectZooKeeperDB() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(URL, USER, PW);
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
	public void createZooKeeper(String id, String name, int age, int genderIndex, int rankIndex, int departmentIndex,
			int isWorkingIndex, int experienceYear, int canHandleDangerAnimalIndex, String desc) {
		String sql = "INSERT INTO zoo_keepers (id, name, age, gender, department, rank_level, is_working, experience_year, can_handle_danger_animal, licenses) values (?,?,?,?,?,?,?,?,?,?)";
		try (Connection connection = connectZooKeeperDB();
				PreparedStatement pstmt = connection.prepareStatement(sql);) {
			pstmt.setString(1, id); // id
			pstmt.setString(2, name); // name
			pstmt.setInt(3, age); // age
			pstmt.setString(4, ZooKeeperConverter.genderConverter(genderIndex).name()); // gender
			pstmt.setString(5, ZooKeeperConverter.departmentConverter(departmentIndex).name()); // department
			pstmt.setString(6, ZooKeeperConverter.rankConverter(rankIndex).name()); // rank
			pstmt.setInt(7, isWorkingIndex); // isWorking
			pstmt.setInt(8, experienceYear); // experienceYear
			pstmt.setInt(9, canHandleDangerAnimalIndex); // canHandleDangerAnimal
			pstmt.setString(10, desc); // desc
			pstmt.executeUpdate();

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 전체사육사를 DB에서 조회하고 리턴하는 메서드 입니다. 
	 * 
	 * @return List<ZooKeeper>
	 */
	public List<ZooKeeper> getZooKeeperListDB() {
		List<ZooKeeper> zooKeepers = new ArrayList<>();
		String sql = "SELECT * FROM zoo_keepers";
		try (Connection connection = connectZooKeeperDB();
				Statement statement = connection.createStatement();
				ResultSet resultset = statement.executeQuery(sql);) {
			while (resultset.next()) {
				List<String> list = stringListMaker(resultset);
				ZooKeeper zooKeeper = new ZooKeeper(resultset.getString("id"), resultset.getString("name"),
						resultset.getInt("age"), Gender.valueOf(resultset.getString("gender")),
						ZooKeeperRank.valueOf(resultset.getString("rank_level")),
						Department.valueOf(resultset.getString("department")),
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
		String sql = "SELECT * FROM zoo_keepers WHERE id = ?";
		try (Connection connection = connectZooKeeperDB();
				PreparedStatement pstmt = connection.prepareStatement(sql);) {
			pstmt.setString(1, id);
			try (ResultSet resultset = pstmt.executeQuery();) {
				while (resultset.next()) {
					List<String> list = stringListMaker(resultset);
					zk = new ZooKeeper(resultset.getString("id"), resultset.getString("name"), resultset.getInt("age"),
							Gender.valueOf(resultset.getString("gender")),
							ZooKeeperRank.valueOf(resultset.getString("rank_level")),
							Department.valueOf(resultset.getString("department")),
							resultset.getInt("is_working") == 1 ? true : false, resultset.getInt("experience_year"),
							resultset.getInt("can_handle_danger_animal") == 1 ? true : false, list);
				}
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
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
		String sql = "SELECT * FROM zoo_keepers WHERE name = ?";
		try (Connection connection = connectZooKeeperDB(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, name);
			try (ResultSet resultset = pstmt.executeQuery()) {
				while (resultset.next()) {
					List<String> list = stringListMaker(resultset);
					ZooKeeper zk = new ZooKeeper(resultset.getString("id"), resultset.getString("name"),
							resultset.getInt("age"), Gender.valueOf(resultset.getString("gender")),
							ZooKeeperRank.valueOf(resultset.getString("rank_level")),
							Department.valueOf(resultset.getString("department")),
							resultset.getInt("is_working") == 1 ? true : false, resultset.getInt("experience_year"),
							resultset.getInt("can_handle_danger_animal") == 1 ? true : false, list);
					zkList.add(zk);
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
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
		String sql = "SELECT * FROM zoo_keepers WHERE department = ?";
		try (Connection connection = connectZooKeeperDB(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
			Department d_enum = ZooKeeperConverter.departmentConverter(index);
			pstmt.setString(1, d_enum.name());
			try (ResultSet resultset = pstmt.executeQuery()) {
				while (resultset.next()) {
					List<String> list = stringListMaker(resultset);
					ZooKeeper zk = new ZooKeeper(resultset.getString("id"), resultset.getString("name"),
							resultset.getInt("age"), Gender.valueOf(resultset.getString("gender")),
							ZooKeeperRank.valueOf(resultset.getString("rank_level")),
							Department.valueOf(resultset.getString("department")),
							resultset.getInt("is_working") == 1 ? true : false, resultset.getInt("experience_year"),
							resultset.getInt("can_handle_danger_animal") == 1 ? true : false, list);
					zooKeepers.add(zk);
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return zooKeepers;
	}

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
	 * 사육사의 재직현황을 수정하는 매서드입니다.
	 * 
	 * @param targetId
	 * @param index
	 * @return int
	 */
	public int editIsWorkingDB(String targetId, int index) {
		int success = 0;
		String sql = "UPDATE zoo_keeper SET is_working = ? WHERE id = ?";
		try (Connection connection = connectZooKeeperDB();
				PreparedStatement pstmt = connection.prepareStatement(sql);) {
			pstmt.setInt(1, index == 1 ? 1 : 0);
			pstmt.setString(2, targetId);
			success = pstmt.executeUpdate();

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return success;
	}

	/** 
	 * 사육사의 위험동물관리여부를 수정할 수 있는 매서드 입니다.
	 * 
	 * @param targetId
	 * @param index
	 * @return int
	 */
	public int editPermissionDangerAnimalDB(String targetId, int index) {
		int success = 0;
		String sql = "UPDATE zoo_keeper SET can_handle_danger_animal = ? WHERE id = ?";
		try (Connection connection = connectZooKeeperDB(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, index == 1 ? 1 : 0);
			pstmt.setString(2, targetId);
			success = pstmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return success;
	}

	public int deleteZooKeeperDB(String targetId) {
		int success = 0;
		String sql = "DELETE FROM zoo_keeper WHERE id = ?";
		try (Connection connection = connectZooKeeperDB(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, targetId);
			success = pstmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return success;
	}

	// Inner Util Methods
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

}
