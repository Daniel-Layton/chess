package dataaccess;

import model.AuthData;
import model.UserData;
import service.models.RegisterRequest;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;
}
