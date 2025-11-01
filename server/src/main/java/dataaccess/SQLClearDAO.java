package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLClearDAO {
    public void clearTables() throws DataAccessException, SQLException {
        System.out.println("INFO - clearSQLDAO hit");
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement1 = "truncate table users";
            var statement2 = "truncate table auth";
            var statement3 = "truncate table games";
            try (PreparedStatement ps = conn.prepareStatement(statement1)) {
                ps.executeUpdate();};
            try (PreparedStatement ps = conn.prepareStatement(statement2)) {
                ps.executeUpdate();};
            try (PreparedStatement ps = conn.prepareStatement(statement3)) {
                ps.executeUpdate();};
        } catch (SQLException e) {
            System.out.println("sql problem in clear tables dao");
            throw new SQLException("internal server error");
        }
    }
}
