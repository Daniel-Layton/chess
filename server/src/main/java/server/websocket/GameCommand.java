package server.websocket;
import chess.ChessPosition;

public class GameCommand {
    private String commandType;
    private String authToken;
    private Integer gameID;
    private MoveData move;

    public String getCommandType() { return commandType; }
    public String getAuthToken() { return authToken; }
    public Integer getGameID() { return gameID; }
    public MoveData getMove() { return move; }
}

// Nested move object
class MoveData {
    private ChessPosition startPosition;
    private ChessPosition endPosition;

    public ChessPosition getStartPosition() { return startPosition; }
    public ChessPosition getEndPosition() { return endPosition; }
}