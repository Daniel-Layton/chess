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
    public void createAuth(AuthData authData) throws DataAccessException {
        System.out.println("INFO - createAuthDAO hit");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "insert into auth (auth_string, username) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("error accessing auth Database");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        System.out.println("INFO - getAuthDAO hit");
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        System.out.println("INFO - deleteAuthDAO hit");

    }
}
