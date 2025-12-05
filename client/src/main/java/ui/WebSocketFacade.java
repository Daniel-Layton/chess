package ui;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final Gson gson = new Gson();

    public interface MessageHandler {
        void handle(ServerMessage message);
    }

    private final MessageHandler handler;

    public WebSocketFacade(String url, MessageHandler handler) throws Exception {
        this.handler = handler;
        url = url.replace("http", "ws") + "/ws";
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(url));
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        session.addMessageHandler(String.class, msg -> {
            try {
                ServerMessage serverMessage = gson.fromJson(msg, ServerMessage.class);
                handler.handle(serverMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onError(Session session, Throwable thr) {
        thr.printStackTrace();
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed: " + closeReason);
    }

    // --------- SEND COMMANDS TO SERVER ---------
    public void connect(String auth, String gameId) throws Exception {
        send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameId));
    }

    public void makeMove(String auth, String gameId) throws Exception {
        send(new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, gameId));
    }

    public void leave(String auth, String gameId) throws Exception {
        send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameId));
    }

    public void resign(String auth, String gameId) throws Exception {
        send(new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameId));
    }

    private void send(UserGameCommand cmd) throws Exception {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(gson.toJson(cmd));
        } else {
            throw new Exception("WebSocket session is not open");
        }
    }
}