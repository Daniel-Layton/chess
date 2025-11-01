package dataaccess;

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
        System.out.println(userData.username());
        System.out.println(userData.password());
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "insert into users (username, password_hash, email, salt) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                String salt = BCrypt.gensalt();
                String hashedPassword = BCrypt.hashpw(userData.password(), salt);
                ps.setString(1, userData.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, userData.email());
                ps.setString(4, salt);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("sql problem in create user dao");
            throw new DataAccessException("sql error");
        } catch (Exception e) {
            throw new DataAccessException("error accessing user Database");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("INFO - getUserDAO hit");
        System.out.println(username);
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "select * from users where username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        String password_hash = resultSet.getString("password_hash");
                        String salt = resultSet.getString("salt");
                        String email = resultSet.getString("email");
                        return new UserData(username, password_hash, email, salt);
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
