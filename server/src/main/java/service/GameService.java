package service;
import chess.ChessGame;
import chess.ChessMove;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import service.models.*;

import java.sql.SQLException;
import java.util.List;

public class GameService {

    SQLGameDAO GameDB = new SQLGameDAO();
    SQLAuthDAO AuthDB = new SQLAuthDAO();
    int GameIDinc = 0;

    public ListResult list(ListRequest listRequest) throws DataAccessException, SQLException {
        AuthData authQuery = AuthDB.getAuth(listRequest.authToken());
        if (authQuery.username() == null) {
            throw new DataAccessException("unauthorized");
        }
        List<GameData> gameList = GameDB.listGames();
        return new ListResult(gameList);
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException, SQLException {
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
        if (game == null) {
            throw new Exception("bad request: Game does not exist");
        }
        System.out.println(authQuery.username() + " is joining game " + game.gameName());

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

    public GameData getGameData(String gameID) throws DataAccessException, SQLException {
        return GameDB.getGame(gameID);
    }

    public String usernameForToken(String authToken) throws DataAccessException, SQLException {
        model.AuthData authQuery = AuthDB.getAuth(authToken);
        if (authQuery == null || authQuery.username() == null) {
            throw new DataAccessException("unauthorized");
        }
        return authQuery.username();
    }

    public void updateGameData(GameData gameData) throws SQLException {
        try {
            GameDB.updateGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public GameData applyMove(String authToken, Integer gameID, ChessMove move) throws Exception {
        String username = usernameForToken(authToken);
        GameData gameData = getGameData(Integer.toString(gameID));
        if (gameData == null) throw new Exception("Game does not exist");

        ChessGame game = gameData.game();

        if (game.isGameOver()) {
            throw new IllegalStateException("Game is already over");
        }

        ChessGame.TeamColor playerColor =
                username.equals(gameData.whiteUsername()) ? ChessGame.TeamColor.WHITE :
                        username.equals(gameData.blackUsername()) ? ChessGame.TeamColor.BLACK :
                                null;

        if (playerColor == null) {
            throw new IllegalStateException("Observer cannot move");
        }

        if (game.getTeamTurn() != playerColor) {
            throw new IllegalStateException("Not your turn");
        }

        var legalMoves = game.validMoves(move.getStartPosition());
        if (legalMoves == null || !legalMoves.contains(move)) {
            throw new IllegalStateException("Invalid move");
        }

        game.makeMove(move);

        GameData updated = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        );

        updateGameData(updated);
        return updated;
    }
}