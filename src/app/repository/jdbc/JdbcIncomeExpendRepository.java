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

	public IncomeExpend createIncomeExpend(IncomeExpend newIE) {
		IncomeExpend ie = null;
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
			ie = newIE;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ie;
	}

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
			e.printStackTrace();
		}
		return list;
	}

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
			e.printStackTrace();
		}
		return list;
	}

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
			e.printStackTrace();
		}
		return 0L;
	}

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
			e.printStackTrace();
		}
		return 0L;
	}
}
