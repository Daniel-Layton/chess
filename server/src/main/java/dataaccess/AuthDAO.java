package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authToken) throws DataAccessException;
}
