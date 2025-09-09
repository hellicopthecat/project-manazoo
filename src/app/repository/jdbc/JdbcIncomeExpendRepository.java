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
import app.incomeExpend.IEConverter;
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

	public int createInEx(String id, long amount, String desc, int ieType, int eventNum) {
		int success = 0;
		String sql = "INSERT INTO(id, amount, description, date, type, event_type) income_expends VALUES (?,?,?,?,?,?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, id);
			pstmt.setLong(2, amount);
			pstmt.setString(3, desc);
			pstmt.setDate(4, Date.valueOf(LocalDate.now()));
			pstmt.setString(5, IEConverter.IETypeConverter(ieType).name());
			pstmt.setString(6, IEConverter.eventTypeConverter(eventNum).name());
			success = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success;
	}

	public List<IncomeExpend> getIncomeList() {
		List<IncomeExpend> list = new ArrayList<>();
		String sql = """
				SELECT *
				FROM income_expend
				WHERE type = INCOME
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
				FROM income_expend
				WHERE type = EXPEND
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
}
