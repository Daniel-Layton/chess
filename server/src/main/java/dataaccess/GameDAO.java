package dataaccess;

import model.GameData;
import service.models.CreateRequest;

public interface GameDAO {
    public void createGame(GameData gameData) throws DataAccessException;
}
