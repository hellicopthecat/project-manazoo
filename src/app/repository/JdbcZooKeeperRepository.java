package app.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import app.zooKeeper.zooKeeperEnum.ZooKeeperConverter;

public class JdbcZooKeeperRepository {

	private Connection connection = null;
	private Statement statement = null;

	private JdbcZooKeeperRepository() {
		connectZooKeeperDB();
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
	private void connectZooKeeperDB() {
		try {
			String url = "jdbc:mysql://127.0.0.1:3306/manazoo";
			String user = "root";
			String password = "1111";
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("db연결에 실패하였습니다.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
		try {
			String sql = "INSERT INTO zoo_keepers (id, name, age, gender, department, rank_level, is_working, experience_year, can_handle_danger_animal, licenses) values (?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pstmt = connection.prepareStatement(sql);
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
			pstmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
