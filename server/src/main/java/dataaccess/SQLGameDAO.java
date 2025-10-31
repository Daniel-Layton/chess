package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO() {
        try {DatabaseManager.createDatabase();}
        catch (DataAccessException e) {
            System.out.println("database exists");
        }
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        System.out.println("INFO - createGameDAO hit");
        System.out.println(gameData.gameName());
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "insert into games (gameID, gameName, blackUsername, whiteUsername, gameData) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                var serializer = new Gson();
                ps.setString(1, gameData.gameID());
                ps.setString(2, gameData.gameName());
                ps.setString(3, gameData.blackUsername());
                ps.setString(4, gameData.whiteUsername());
                ps.setString(5, serializer.toJson(gameData.game()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("sql problem in create game dao");
            throw new DataAccessException("sql error");
        } catch (Exception e) {
            throw new DataAccessException("error accessing auth Database");
        }
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        System.out.println("INFO - getGameDAO hit");
        System.out.println(gameID);
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "select * from games where gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                var serializer = new Gson();
                ps.setString(1, gameID);
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        String gameName = resultSet.getString("gameName");
                        String blackUsername = resultSet.getString("blackUsername");
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String gameData = resultSet.getString("gameData");
                        return new GameData(gameID, gameName, blackUsername, whiteUsername, serializer.fromJson(gameData, ChessGame.class));
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("sql problem in create game dao");
            throw new DataAccessException("sql error");
        } catch (Exception e) {
            throw new DataAccessException("error accessing auth Database");
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        System.out.println("INFO - updateGameDAO hit");
        System.out.println(gameData.gameName());
        System.out.println(" ");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET gameData = ? WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                var serializer = new Gson();
                ps.setString(1, gameData.gameID());
                ps.setString(2, serializer.toJson(gameData.game()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("sql problem in create game dao");
            throw new DataAccessException("sql error");
        } catch (Exception e) {
            throw new DataAccessException("error accessing auth Database");
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        System.out.println("INFO - listGamesDAO hit");
        System.out.println(" ");
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameName, blackUsername, whiteUsername, gameData FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                var serializer = new Gson();
                List<GameData> gamesList = new ArrayList<>();

                ResultSet resultSet = ps.executeQuery();
                    while (resultSet.next()) {
                        String gameID = resultSet.getString("gameID");
                        String gameName = resultSet.getString("gameName");
                        System.out.println(gameName);
                        String blackUsername = resultSet.getString("blackUsername");
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String gameData = resultSet.getString("gameData");
                        ChessGame chessGame = serializer.fromJson(gameData, ChessGame.class);

                        GameData game = new GameData(gameID, gameName, blackUsername, whiteUsername, chessGame);
                        gamesList.add(game);
                    }
                System.out.println("Returning " + gamesList.size() + " gameData");
                return gamesList;
            }
        } catch (SQLException e) {
            System.out.println("sql problem in create game dao");
            throw new DataAccessException("sql error");
        } catch (Exception e) {
            throw new DataAccessException("error accessing auth Database");
        }
    }
}
