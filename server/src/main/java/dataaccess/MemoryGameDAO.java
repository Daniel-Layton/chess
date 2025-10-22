package dataaccess;

import model.GameData;
import model.UserData;
import service.models.CreateRequest;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<String, GameData> games = DataBank.getInstance().games;

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }
}
