package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLGameDAO implements GameDAO{
    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        System.out.println("INFO - createGameDAO hit");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "insert into games (gameID, gameName, blackUsername, whiteUsername, gameData) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                var serializer = new Gson();
                ps.setString(1, gameData.gameID());
                ps.setString(2, gameData.gameName());
                ps.setString(3, gameData.blackUsername());
                ps.setString(4, gameData.whiteUsername());
                ps.setString(5, serializer.toJson(gameData.game()));
                System.out.println(serializer.toJson(gameData.game()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("sql problem in create game dao");
            throw new DataAccessException("sql error");
        } catch (Exception e) {
            System.out.println("well there's your problem!");
            throw new DataAccessException("error accessing auth Database");
        }
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        System.out.println("INFO - createGameDAO hit");

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
                        return new UserData(gameName, blackUsername, whiteUsername, serializer.fromJson(__________));
                    } else {
                        return null;
                    }
            }
        } catch (SQLException e) {
            System.out.println("sql problem in create game dao");
            throw new DataAccessException("sql error");
        } catch (Exception e) {
            System.out.println("well there's your problem!");
            throw new DataAccessException("error accessing auth Database");
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return List.of();
    }
}
