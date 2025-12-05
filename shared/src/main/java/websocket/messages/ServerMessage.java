package websocket.messages;

import chess.ChessGame;

public class ServerMessage {
    public ServerMessageType serverMessageType;

    public ChessGame game;
    public String errorMessage;
    public String message;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessage(ServerMessageType type, ChessGame game) {
        this.serverMessageType = type;
        this.game = game;
    }

    public ServerMessage(ServerMessageType type, String text) {
        this.serverMessageType = type;
        if (type == ServerMessageType.ERROR) this.errorMessage = text;
        if (type == ServerMessageType.NOTIFICATION) this.message = text;
    }

    public ServerMessageType getServerMessageType() { return this.serverMessageType; }
    public ChessGame getGame() { return game; }
    public String getErrorMessage() { return errorMessage; }
    public String getMessage() { return message; }
}
