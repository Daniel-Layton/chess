package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.models.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    MemoryUserDAO UserDB = new MemoryUserDAO();
    SQLAuthDAO AuthDB = new SQLAuthDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, DataAccessException {
//        System.out.println("Hit Register Service!!!");
        UserData query = UserDB.getUser(registerRequest.username());
        if (query != null) throw new AlreadyTakenException("Username already taken");
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        UserDB.createUser(newUser);
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), registerRequest.username());
        AuthDB.createAuth(newAuth);
        RegisterResult result = new RegisterResult(registerRequest.username(), newAuth.authToken());
        return result;
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData query = UserDB.getUser(loginRequest.username());
        if (query == null || !Objects.equals(query.password(), loginRequest.password())) throw new DataAccessException("unauthorized");
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