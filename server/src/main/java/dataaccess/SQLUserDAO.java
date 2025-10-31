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
            throw new DataAccessException("error accessing auth Database");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
