package service;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import service.models.*;

import java.util.UUID;

public class GameService {

    MemoryGameDAO GameDB = new MemoryGameDAO();
    MemoryAuthDAO AuthDB = new MemoryAuthDAO();
    int GameIDinc = 0;

    public ListResult list(ListRequest listRequest) throws AlreadyTakenException {
        return null;
    }
    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        AuthData authQuery = AuthDB.getAuth(createRequest.authToken());
        if (authQuery.username() == null) throw new DataAccessException("unauthorized");
        GameIDinc++;
        GameData newGame = new GameData(Integer.toString(GameIDinc), null, null, createRequest.gameName(), new ChessGame());
        GameDB.createGame(newGame);
        return new CreateResult(Integer.toString(GameIDinc));
    }
    public JoinResult join(JoinRequest joinRequest) {
        return null;
    }
}