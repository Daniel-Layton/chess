package dataaccess;

import model.AuthData;

import java.sql.*;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() {
        try {DatabaseManager.createDatabase();}
        catch (DataAccessException e) {
            System.out.println("database exists");
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException, SQLException {
        System.out.println("INFO - createAuthDAO hit");
        System.out.println(authData.username());
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "insert into auth (auth_string, username) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException("internal server error");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, SQLException {
        System.out.println("INFO - getAuthDAO hit");
        System.out.println(authToken);
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "select username from auth where auth_string = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("username");
                        return new AuthData(authToken, username);
                    } else {
                        throw new DataAccessException("error: unauthenticated");
                    }
                }
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("error: unauthenticated");
        } catch (SQLException e) {
            throw new SQLException("internal server error");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, SQLException {
        System.out.println("INFO - deleteAuthDAO hit");
        System.out.println(authToken);
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "delete from auth where auth_string = ?";
            int rows_affected = 0;
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                rows_affected = ps.executeUpdate();
                System.out.println(rows_affected + " deletions from auth table");
            }
        } catch (SQLException e) {
            throw new SQLException("internal server error");
        }
    }
}
