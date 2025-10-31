package service;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import service.models.*;

import java.util.List;

public class GameService {

    SQLGameDAO GameDB = new SQLGameDAO();
    SQLAuthDAO AuthDB = new SQLAuthDAO();
    int GameIDinc = 0;

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        AuthData authQuery = AuthDB.getAuth(listRequest.authToken());
        if (authQuery.username() == null) {
            throw new DataAccessException("unauthorized");
        }
        List<GameData> gameList = GameDB.listGames();
        return new ListResult(gameList);
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        AuthData authQuery = AuthDB.getAuth(createRequest.authToken());
        if (authQuery.username() == null) throw new DataAccessException("unauthorized");
        GameIDinc++;
        GameData newGame = new GameData(Integer.toString(GameIDinc), null, null, createRequest.gameName(), new ChessGame());
        GameDB.createGame(newGame);
        return new CreateResult(Integer.toString(GameIDinc));
    }

    public JoinResult join(JoinRequest joinRequest) throws Exception {
        AuthData authQuery = AuthDB.getAuth(joinRequest.authToken());
        if (authQuery.username() == null) throw new DataAccessException("unauthorized");
        GameData game = GameDB.getGame(joinRequest.gameID());

        if (joinRequest.playerColor() == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() == null) {
                GameData newGameData = new GameData(game.gameID(), game.whiteUsername(), authQuery.username(), game.gameName(), game.game());
                GameDB.updateGame(newGameData);
                return new JoinResult();
            }
            else {
                throw new AlreadyTakenException("black already taken");
            }
        }
        else if (joinRequest.playerColor() == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() == null) {
                GameData newGameData = new GameData(game.gameID(), authQuery.username(), game.blackUsername(), game.gameName(), game.game());
                GameDB.updateGame(newGameData);
                return new JoinResult();
            }
            else {
                throw new AlreadyTakenException("white already taken");
            }
        }
        else {
            throw new Exception("bad team color");
        }
    }
}