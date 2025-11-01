package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException, SQLException;

    AuthData getAuth(String authToken) throws DataAccessException, SQLException;

    void deleteAuth(String authToken) throws DataAccessException, SQLException;
}
