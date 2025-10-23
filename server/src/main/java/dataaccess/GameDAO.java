package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;

    GameData getGame(String gameID) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;
}
