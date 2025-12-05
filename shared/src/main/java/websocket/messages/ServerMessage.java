package websocket.messages;

import chess.ChessGame;

public class ServerMessage {

    private final ServerMessageType serverMessageType;

    private final ChessGame game;
    private final String errorMessage;
    private final String message;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }
    public static ServerMessage loadGame(ChessGame game) {
        return new ServerMessage(ServerMessageType.LOAD_GAME, game, null, null);
    }
    public static ServerMessage error(String errorMessage) {
        return new ServerMessage(ServerMessageType.ERROR, null, errorMessage, null);
    }
    public static ServerMessage notification(String message) {
        return new ServerMessage(ServerMessageType.NOTIFICATION, null, null, message);
    }
    private ServerMessage(ServerMessageType type,
                          ChessGame game,
                          String errorMessage,
                          String message) {
        this.serverMessageType = type;
        this.game = game;
        this.errorMessage = errorMessage;
        this.message = message;
    }

    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getMessage() {
        return message;
    }
}