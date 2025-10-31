package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() {
        try {DatabaseManager.createDatabase();}
        catch (DataAccessException e) {
            System.out.println("database exists");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        System.out.println("INFO - createUserDAO hit");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "insert into users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
                ps.setString(1, userData.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, userData.email());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("well there's your problem!");
            throw new DataAccessException("error accessing auth Database");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("INFO - getUserDAO hit");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "select * from users where username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        String password_hash = resultSet.getString("password");
                        String email = resultSet.getString("email");
                        return new UserData(username, password_hash, email);
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("error accessing user Database");
        }
    }
}
