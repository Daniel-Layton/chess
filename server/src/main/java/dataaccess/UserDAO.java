package dataaccess;
import model.UserData;
import service.models.RegisterRequest;

import java.sql.SQLException;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException, SQLException;

    UserData getUser(String username) throws DataAccessException, SQLException;
}
