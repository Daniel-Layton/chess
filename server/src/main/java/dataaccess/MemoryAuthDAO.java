package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, String> auths = DataBank.getInstance().auths;

    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.authToken(), authData.username());
    }
}
