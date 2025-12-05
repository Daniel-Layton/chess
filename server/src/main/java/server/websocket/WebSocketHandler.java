package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import service.GameService;
import service.UserService;
import model.GameData;
import service.models.RegisterRequest;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();

    private final GameService gameService;
    private final UserService userService;

    public WebSocketHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    public void handleConnect(WsConnectContext ctx) {
        System.out.println("WS connected: " + ctx.session);
    }

    public void handleMessage(WsMessageContext ctx) {
        String json = ctx.message();
        WsContext ws = ctx;

        UserGameCommand cmd;
        try {
            cmd = gson.fromJson(json, UserGameCommand.class);
        } catch (Exception e) {
            sendErrorToSession(ws, "Error: malformed command JSON");
            return;
        }
        if (cmd == null || cmd.getCommandType() == null) {
            sendErrorToSession(ws, "Error: malformed command");
            return;
        }

        switch (cmd.getCommandType()) {
            case CONNECT -> handleUserConnect(ws, cmd);
            case MAKE_MOVE -> handleMakeMove(ws, cmd, json);
            case LEAVE -> handleLeave(ws, cmd);
            case RESIGN -> handleResign(ws, cmd);
            default -> sendErrorToSession(ws, "Error: unknown command type");
        }
    }

    public void handleClose(WsCloseContext ctx) {
        WsContext ws = ctx;
        if (connections.contains(ws)) {
            String name = connections.usernameForSession(ws);
            connections.remove(ws);
            System.out.println("WS closed: " + name);
        } else {
            System.out.println("WS closed (untracked session)");
        }
    }

    private void sendErrorToSession(WsContext ws, String message) {
        ws.send(gson.toJson(ServerMessage.error(message)));
    }

    private void handleUserConnect(WsContext ws, UserGameCommand cmd) {
        try {
            if (cmd.getAuthToken() == null || cmd.getGameID() == null) {
                sendErrorToSession(ws, "Error: missing authToken or gameID");
                return;
            }

            String username = gameService.usernameForToken(cmd.getAuthToken());
            GameData gameData = gameService.getGameData(String.valueOf(cmd.getGameID()));
            if (gameData == null) {
                sendErrorToSession(ws, "Error: game not found");
                return;
            }

            connections.add(ws, cmd.getGameID(), username);

            ServerMessage load = ServerMessage.loadGame(gameData.game());
            ws.send(gson.toJson(load));

            String playerType = username.equals(gameData.whiteUsername()) ? "white"
                    : username.equals(gameData.blackUsername()) ? "black" : "observer";

            String notifText = username + " connected as " + playerType;
            ServerMessage notify = ServerMessage.notification(notifText);
            connections.broadcastToGame(cmd.getGameID(), ws, notify);

        } catch (Exception e) {
            sendErrorToSession(ws, "Error: " + e.getMessage());
        }
    }

    private void handleMakeMove(WsContext ws, UserGameCommand cmd, String rawJson) {
        if (cmd.getAuthToken() == null || cmd.getGameID() == null) {
            sendErrorToSession(ws, "Error: missing authToken or gameID");
            return;
        }

        try {
            GameData gameData = gameService.getGameData(String.valueOf(cmd.getGameID()));
            if (gameData.game().isGameOver()) {
                sendErrorToSession(ws, "Error: game is already over");
                return;
            }

            var serializer = new Gson();
            GameCommand gameCommand = serializer.fromJson(rawJson, GameCommand.class);

            if (gameCommand.getMove() == null || gameCommand.getMove().getStartPosition() == null || gameCommand.getMove().getEndPosition() == null) {
                sendErrorToSession(ws, "Error: missing move positions");
                return;
            }

            ChessMove move = new ChessMove(gameCommand.getMove().getStartPosition(), gameCommand.getMove().getEndPosition(), null);

            GameData updated = gameService.applyMove(cmd.getAuthToken(), cmd.getGameID(), move);

            String mover = gameService.usernameForToken(cmd.getAuthToken());

            ServerMessage load = ServerMessage.loadGame(updated.game());
            connections.broadcastToGame(cmd.getGameID(), null, load);

            ServerMessage notify = ServerMessage.notification(mover + " made a move");
            connections.broadcastToGame(cmd.getGameID(), ws, notify);

        } catch (Exception e) {
            sendErrorToSession(ws, "Error: " + e.getMessage());
            return;
        }
    }

    private void handleLeave(WsContext ws, UserGameCommand cmd) {
        try {
            if (cmd.getAuthToken() == null || cmd.getGameID() == null) {
                sendErrorToSession(ws, "Error: missing authToken or gameID");
                return;
            }

            String username = gameService.usernameForToken(cmd.getAuthToken());
            GameData gameData = gameService.getGameData(String.valueOf(cmd.getGameID()));
            if (gameData == null) {
                sendErrorToSession(ws, "Error: game not found");
                return;
            }

            String white = gameData.whiteUsername();
            String black = gameData.blackUsername();

            if (username.equals(white)) {
                white = null;
            } else if (username.equals(black)) {
                black = null;
            }

            GameData updated = new GameData(
                    gameData.gameID(),
                    white,
                    black,
                    gameData.gameName(),
                    gameData.game()
            );

            gameService.updateGameData(updated);

            ServerMessage notify = ServerMessage.notification(username + " left the game");
            connections.broadcastToGame(cmd.getGameID(), ws, notify);

            connections.remove(ws);

        } catch (Exception e) {
            sendErrorToSession(ws, "Error: " + e.getMessage());
        }
    }

    private void handleResign(WsContext ws, UserGameCommand cmd) {
        try {
            if (cmd.getAuthToken() == null || cmd.getGameID() == null) {
                sendErrorToSession(ws, "Error: missing authToken or gameID");
                return;
            }

            GameData gameData = gameService.getGameData(String.valueOf(cmd.getGameID()));
            if (gameData == null) {
                sendErrorToSession(ws, "Error: game not found");
                return;
            }

            ChessGame game = gameData.game();

            if (game.isGameOver()) {
                sendErrorToSession(ws, "Error: game is already over");
                return;
            }

            gameService.resignGame(cmd.getAuthToken(), cmd.getGameID());

            String username = gameService.usernameForToken(cmd.getAuthToken());
            ServerMessage notify = ServerMessage.notification(username + " resigned");
            connections.broadcastToGame(cmd.getGameID(), null, notify);

        } catch (Exception e) {
            sendErrorToSession(ws, "Error: " + e.getMessage());
        }
    }
}