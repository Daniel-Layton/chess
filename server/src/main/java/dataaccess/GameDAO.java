package dataaccess;

import model.GameData;
import service.models.CreateRequest;

import java.util.List;

public interface GameDAO {
    public void createGame(GameData gameData) throws DataAccessException;

    public GameData getGame(String gameID) throws DataAccessException;

    public void updateGame(GameData gameData) throws DataAccessException;

    public List<GameData> listGames() throws DataAccessException;
}
