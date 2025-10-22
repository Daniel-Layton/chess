package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.models.*;

import java.util.UUID;

public class UserService {

    MemoryUserDAO UserDB = new MemoryUserDAO();
    MemoryAuthDAO AuthDB = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException {
        System.out.println("Hit Register Service!!!");
        UserData query = UserDB.getUser(registerRequest.username());
        if (query != null) throw new AlreadyTakenException("Username already taken");
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        UserDB.createUser(newUser);
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), registerRequest.username());
        AuthDB.createAuth(newAuth);
        RegisterResult result = new RegisterResult(registerRequest.username(), newAuth.authToken());
        return result;
    }
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {
    }
}