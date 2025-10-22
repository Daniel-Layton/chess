package dataaccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;

public class DataBank {

    private static DataBank single_instance = null;

    public final HashMap<String, UserData> users;
    public final HashMap<String, String> auths;
    public final HashMap<String, GameData> games;

    // Constructor
    private DataBank()
    {
        this.users = new HashMap<>();
        this.auths = new HashMap<>();
        this.games = new HashMap<>();
    }

    public static synchronized DataBank getInstance()
    {
        if (single_instance == null)
            single_instance = new DataBank();

        return single_instance;
    }
}
