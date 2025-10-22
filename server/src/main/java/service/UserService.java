package service;
import dataaccess.AlreadyTakenException;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.models.*;
import dataaccess.UserDAO;

import java.util.UUID;

public class UserService {

    MemoryUserDAO database = new MemoryUserDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException {
        System.out.println("Hit Register Service!!!");
        UserData query = database.getUser(registerRequest.username());
        if (query != null) throw new AlreadyTakenException("Username already taken");
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        database.createUser(newUser);
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), registerRequest.username());
        database.createAuth(newAuth);
        RegisterResult result = new RegisterResult(registerRequest.username(), newAuth.authToken());
        return result;
    }
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {
    }
}