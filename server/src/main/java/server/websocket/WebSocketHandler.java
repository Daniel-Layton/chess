package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import service.GameService;
import service.UserService;
import model.GameData;
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

    // WS connect
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("WS connected: " + ctx.session);
    }

    // WS message
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

    // WS close
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
        try {
            ErrorMessage em = new ErrorMessage(message); // Correct ERROR type
            ws.send(gson.toJson(em));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

            // Send LOAD_GAME message
            LoadGameMessage load = new LoadGameMessage(gameData);
            ws.send(gson.toJson(load));

            // Notify others
            String playerType = username.equals(gameData.whiteUsername()) ? "white"
                    : username.equals(gameData.blackUsername()) ? "black" : "observer";

            String notifText = username + " connected as " + playerType;
            NotificationMessage notify = new NotificationMessage(notifText);
            connections.broadcastToGame(cmd.getGameID(), ws, notify);

        } catch (Exception e) {
            sendErrorToSession(ws, "Error: " + e.getMessage());
        }
    }

    private void handleMakeMove(WsContext ws, UserGameCommand cmd, String rawJson) {
        try {
            if (cmd.getAuthToken() == null || cmd.getGameID() == null) {
                sendErrorToSession(ws, "Error: missing authToken or gameID");
                return;
            }

            Object move = null; // TODO: parse actual move from JSON
            GameData updated = gameService.applyMove(cmd.getAuthToken(), cmd.getGameID(), move);

            LoadGameMessage load = new LoadGameMessage(updated);
            connections.broadcastToGame(cmd.getGameID(), null, load);

            String username = gameService.usernameForToken(cmd.getAuthToken());
            NotificationMessage notify = new NotificationMessage(username + " made a move");
            connections.broadcastToGame(cmd.getGameID(), ws, notify);

        } catch (Exception e) {
            sendErrorToSession(ws, "Error: " + e.getMessage());
        }
    }

    private void handleLeave(WsContext ws, UserGameCommand cmd) {
        try {
            if (cmd.getAuthToken() == null || cmd.getGameID() == null) {
                sendErrorToSession(ws, "Error: missing authToken or gameID");
                return;
            }

            String username = gameService.usernameForToken(cmd.getAuthToken());
            NotificationMessage notify = new NotificationMessage(username + " left the game");
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

            String username = gameService.usernameForToken(cmd.getAuthToken());
            NotificationMessage notify = new NotificationMessage(username + " resigned");
            connections.broadcastToGame(cmd.getGameID(), null, notify);

        } catch (Exception e) {
            sendErrorToSession(ws, "Error: " + e.getMessage());
        }
    }
}