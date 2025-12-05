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
        final String sessionId;

        ConnectionInfo(int gameId, String username, String sessionId) {
            this.gameId = gameId;
            this.username = username;
            this.sessionId = sessionId;
        }
    }

    private final Map<String, ConnectionInfo> connections = new ConcurrentHashMap<>();
    private final Map<String, WsContext> sessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(WsContext ctx, int gameId, String username) {
        String sid = ctx.sessionId();
        connections.put(sid, new ConnectionInfo(gameId, username, sid));
        sessions.put(sid, ctx);
    }

    public void remove(WsContext ctx) {
        String sid = ctx.sessionId();
        connections.remove(sid);
        sessions.remove(sid);
    }

    public boolean contains(WsContext ctx) {
        return connections.containsKey(ctx.sessionId());
    }

    public String usernameForSession(WsContext ctx) {
        ConnectionInfo info = connections.get(ctx.sessionId());
        return info != null ? info.username : null;
    }

    public void broadcastToGame(int gameId, WsContext exclude, ServerMessage message) {
        String json = gson.toJson(message);
        String excludeId = (exclude == null ? null : exclude.sessionId());

        connections.forEach((sid, info) -> {
            if (info.gameId == gameId && !sid.equals(excludeId)) {
                WsContext ctx = sessions.get(sid);
                if (ctx != null) {
                    try {
                        ctx.send(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}