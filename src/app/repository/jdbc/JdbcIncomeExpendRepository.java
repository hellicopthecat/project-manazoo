package app.repository.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import app.config.DatabaseConnection;
import app.incomeExpend.EventType;
import app.incomeExpend.IncomeExpend;
import app.incomeExpend.IncomeExpendType;

public class JdbcIncomeExpendRepository {

	private JdbcIncomeExpendRepository() {

	}

	private static class SingletonHolder {
		private static final JdbcIncomeExpendRepository INSTANCE = new JdbcIncomeExpendRepository();
	}

	public static JdbcIncomeExpendRepository getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * IncomeExpend를 생성하는 메서드입니다.
	 * 
	 * @param newIE
	 * @return IncomeExpend
	 */
	public IncomeExpend createIncomeExpend(IncomeExpend newIE) {
		String sql = """
				INSERT INTO income_expends
				(id, amount, description, date, type, event_type)
				VALUES (?,?,?,?,?,?)
				""";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, newIE.getId());
			pstmt.setLong(2, newIE.getMoney());
			pstmt.setString(3, newIE.getDesc());
			pstmt.setDate(4, Date.valueOf(LocalDate.now()));
			pstmt.setString(5, newIE.getIEType().name());
			pstmt.setString(6, newIE.getEventType().name());
			pstmt.executeUpdate();
			return newIE;
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 저장에 실패했습니다. " + e.getMessage(), e);
		}
	}

	/**
	 * 예약을 생성해 IncomeExpend를 작성해주는 메서드입니다.
	 * 
	 * @param newIE
	 * @param id
	 * @return IncomeExpend
	 */
	public IncomeExpend createInExReservation(IncomeExpend newIE, String id) {
		IncomeExpend ie = null;
		String sql = """
				INSERT INTO income_expends
				(id, amount, description, date, type, event_type,reservation_id)
				VALUES (?,?,?,?,?,?,?)
				""";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, newIE.getId());
			pstmt.setLong(2, newIE.getMoney());
			pstmt.setString(3, newIE.getDesc());
			pstmt.setDate(4, Date.valueOf(LocalDate.now()));
			pstmt.setString(5, newIE.getIEType().name());
			pstmt.setString(6, newIE.getEventType().name());
			pstmt.setString(7, id);
			pstmt.executeUpdate();
			ie = newIE;
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 저장에 실패했습니다. " + e.getMessage(), e);
		}
		return ie;
	}

	/**
	 * 사육사의 급여를 생성해 InEx를 생성후 누적급여를 가산해주는 메서드입니다.
	 * 4백만원 이상시 트랜잭션이 발동됩니다.
	 * 
	 * @param newIE
	 * @param id
	 * @return IncomeExpend
	 * @throws SQLException
	 */
	public IncomeExpend createInExSalary(IncomeExpend newIE, String id) throws SQLException {
		String salarySql = """
				INSERT INTO income_expends
				(id, amount, description, date, type, event_type, zookeeper_id)
				VALUES (?,?,?,?,?,?,?)
				""";
		String zooKeeperSql = """
				UPDATE zoo_keepers
				SET salary = salary + ?
				WHERE id = ?
				""";
		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();
			conn.setAutoCommit(false);
			try (PreparedStatement ie_pstmt = conn.prepareStatement(salarySql);
					PreparedStatement zooKeeper_pstmt = conn.prepareStatement(zooKeeperSql)) {
				if (newIE.getMoney() > 4000000) {
					throw new SQLException("4백만원");
				}
				ie_pstmt.setString(1, newIE.getId());
				ie_pstmt.setLong(2, newIE.getMoney());
				ie_pstmt.setString(3, newIE.getDesc());
				ie_pstmt.setDate(4, Date.valueOf(LocalDate.now()));
				ie_pstmt.setString(5, newIE.getIEType().name());
				ie_pstmt.setString(6, newIE.getEventType().name());
				ie_pstmt.setString(7, id);
				ie_pstmt.executeUpdate();
				zooKeeper_pstmt.setLong(1, newIE.getMoney());
				zooKeeper_pstmt.setString(2, id);
				zooKeeper_pstmt.executeUpdate();
			}
			conn.commit();
			return newIE;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	/**
	 * 수입모델리스트를 표출하는 DB메서드 입니다.
	 * 
	 * 
	 * @return List<IncomeExpend>
	 */
	public List<IncomeExpend> getIncomeList() {
		List<IncomeExpend> list = new ArrayList<>();
		String sql = """
				SELECT *
				FROM income_expends
				WHERE type = 'INCOME'
				""";
		try (Connection conn = DatabaseConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet resultset = stmt.executeQuery(sql)) {
			while (resultset.next()) {
				IncomeExpend ie = new IncomeExpend(resultset.getString("id"), resultset.getLong("amount"),
						resultset.getString("description"), IncomeExpendType.valueOf(resultset.getString("type")),
						EventType.valueOf(resultset.getString("event_type")));
				list.add(ie);
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터를 불러오는데 오류가 발생했습니다." + e.getMessage(), e);
		}
		return list;
	}

	/**
	 * 지출모델리스트를 표출하는 DB메서드 입니다.
	 * 
	 * 
	 * @return List<IncomeExpend>
	 */
	public List<IncomeExpend> getExpendList() {
		List<IncomeExpend> list = new ArrayList<>();
		String sql = """
				SELECT *
				FROM income_expends
				WHERE type = 'EXPENSE'
				""";
		try (Connection conn = DatabaseConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet resultset = stmt.executeQuery(sql)) {
			while (resultset.next()) {
				IncomeExpend ie = new IncomeExpend(resultset.getString("id"), resultset.getLong("amount"),
						resultset.getString("description"), IncomeExpendType.valueOf(resultset.getString("type")),
						EventType.valueOf(resultset.getString("event_type")));
				list.add(ie);
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터를 불러오는데 오류가 발생했습니다." + e.getMessage(), e);
		}
		return list;
	}

	/**
	 * 전체 수입을 표출하는 DB메서드 입니다.
	 * 
	 * 
	 * @return Long
	 */
	public Long getTotalIncomes() {
		String sql = """
				SELECT SUM(amount) AS total
				FROM income_expends
				WHERE type = 'INCOME'
				""";
		try (Connection conn = DatabaseConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				// NULL 처리: 수입이 하나도 없으면 SUM 결과가 NULL이므로 0L로 반환
				return rs.getLong("total");
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터를 처리하는데 오류가 발생했습니다." + e.getMessage(), e);
		}
		return 0L;
	}

	/**
	 * 전체 지출을 표출하는 DB메서드 입니다.
	 * 
	 * 
	 * @return Long
	 */
	public Long getTotalExpends() {
		String sql = """
				SELECT SUM(amount) AS total
				FROM income_expends
				WHERE type = 'EXPENSE'
				""";
		try (Connection conn = DatabaseConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				// NULL 처리: 수입이 하나도 없으면 SUM 결과가 NULL이므로 0L로 반환
				return rs.getLong("total");
			}
		} catch (SQLException e) {
			throw new RuntimeException("데이터를 처리하는데 오류가 발생했습니다." + e.getMessage(), e);
		}
		return 0L;
	}
}
