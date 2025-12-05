package server.websocket;

import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private static class ConnectionInfo {
        final int gameId;
        final String username;

        ConnectionInfo(int gameId, String username) {
            this.gameId = gameId;
            this.username = username;
        }
    }

    private final Map<WsContext, ConnectionInfo> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(WsContext ctx, int gameId, String username) {
        connections.put(ctx, new ConnectionInfo(gameId, username));
    }

    public void remove(WsContext ctx) {
        connections.remove(ctx);
    }

    public boolean contains(WsContext ctx) {
        return connections.containsKey(ctx);
    }

    public String usernameForSession(WsContext ctx) {
        ConnectionInfo info = connections.get(ctx);
        return info != null ? info.username : null;
    }

    public void broadcastToGame(int gameId, WsContext exclude, ServerMessage message) {
        String json = gson.toJson(message);

        connections.forEach((ctx, info) -> {
            if (info.gameId == gameId && ctx != exclude) {
                try {
                    ctx.send(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}