package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.models.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    SQLUserDAO UserDB = new SQLUserDAO();
    SQLAuthDAO AuthDB = new SQLAuthDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, DataAccessException {
        UserData query = UserDB.getUser(registerRequest.username());
        if (query != null) throw new AlreadyTakenException("Username already taken");
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email(), null);
        UserDB.createUser(newUser);
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), registerRequest.username());
        AuthDB.createAuth(newAuth);
        RegisterResult result = new RegisterResult(registerRequest.username(), newAuth.authToken());
        return result;
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        System.out.println("INFO - login service hit");
        System.out.println(loginRequest.username() + " is trying to log in");
        UserData query = UserDB.getUser(loginRequest.username());
        System.out.println(query.username());
        System.out.println(loginRequest.password());
        System.out.println(query.password());
        System.out.println(BCrypt.hashpw(loginRequest.password(), query.salt()));
        if (query == null || !Objects.equals(query.password(), BCrypt.hashpw(loginRequest.password(), query.salt()))) {
            System.out.println("login password mismatch / user not found");
            throw new DataAccessException("unauthorized");
        }
        System.out.println(loginRequest.username() + " is not in the database");
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), loginRequest.username());
        AuthDB.createAuth(newAuth);
        LoginResult result = new LoginResult(newAuth.authToken(), newAuth.username());
        return result;
    }
    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData query = AuthDB.getAuth(logoutRequest.authToken());
        if (query.username() == null) throw new DataAccessException("unauthorized");
        AuthDB.deleteAuth(logoutRequest.authToken());
        return new LogoutResult();
    }
}