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

import java.sql.SQLOutput;

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
        if (cmd.getCommandType() == UserGameCommand.CommandType.PING) return;
        if (cmd == null || cmd.getCommandType() == null) {
            sendErrorToSession(ws, "Error: malformed command");
            return;
        }

        switch (cmd.getCommandType()) {
            case CONNECT -> handleUserConnect(ws, cmd);
            case MAKE_MOVE -> handleMakeMove(ws, cmd);
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

    private void handleMakeMove(WsContext ws, UserGameCommand cmd) {
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


            if (cmd.getMove() == null || cmd.getMove().getStartPosition() == null || cmd.getMove().getEndPosition() == null) {
                sendErrorToSession(ws, "Error: missing move positions");
                return;
            }

            ChessMove move = new ChessMove(cmd.getMove().getStartPosition(), cmd.getMove().getEndPosition(), cmd.getMove().getPromotionPiece());

            GameData updated = gameService.applyMove(cmd.getAuthToken(), cmd.getGameID(), move);
            ChessGame updatedGame = updated.game();
            String mover = gameService.usernameForToken(cmd.getAuthToken());

            ServerMessage load = ServerMessage.loadGame(updated.game());
            connections.broadcastToGame(cmd.getGameID(), null, load);

            ServerMessage notify = ServerMessage.notification(mover + " made a move");
            connections.broadcastToGame(cmd.getGameID(), ws, notify);

            ChessGame.TeamColor nextTurn = updatedGame.getTeamTurn();

            boolean inCheck = updatedGame.isInCheck(nextTurn);
            boolean inCheckmate = updatedGame.isInCheckmate(nextTurn);
            boolean inStalemate = updatedGame.isInStalemate(nextTurn);

            if (inCheckmate) {
                connections.broadcastToGame(
                        cmd.getGameID(),
                        null,
                        ServerMessage.notification("Checkmate! " + mover + " wins.")
                );
            } else if (inStalemate) {
                connections.broadcastToGame(
                        cmd.getGameID(),
                        null,
                        ServerMessage.notification("Stalemate! Game ends in a draw.")
                );
            } else if (inCheck) {
                connections.broadcastToGame(
                        cmd.getGameID(),
                        null,
                        ServerMessage.notification("Check against " + nextTurn)
                );
            }

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

            System.out.println(gameData);
            System.out.println("");
            System.out.println(updated);
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

            String username = gameService.usernameForToken(cmd.getAuthToken());

            boolean isWhite = username.equals(gameData.whiteUsername());
            boolean isBlack = username.equals(gameData.blackUsername());

            if (!isWhite && !isBlack) {
                sendErrorToSession(ws,
                        "Error: observers cannot resign");
                return;
            }

            gameService.resignGame(cmd.getAuthToken(), cmd.getGameID());

            String winner = isWhite ? gameData.blackUsername() : gameData.whiteUsername();
            String loseColor = isWhite ? "White" : "Black";
            String winColor  = isWhite ? "Black" : "White";

            connections.broadcastToGame(
                    cmd.getGameID(),
                    null,
                    ServerMessage.notification(
                            username + " resigned. " + winColor + " (" + winner + ") wins!"
                    )
            );

        } catch (Exception e) {
            sendErrorToSession(ws, "Error: " + e.getMessage());
        }
    }
}