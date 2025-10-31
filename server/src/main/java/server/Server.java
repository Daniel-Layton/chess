package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;

import service.ClearService;
import service.GameService;
import service.UserService;
import service.models.*;

public class Server {

    private final Javalin javalin;
    UserService userService = new UserService();
    GameService gameService = new GameService();
    ClearService clearService = new ClearService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::RegisterHandler);
        javalin.post("/session", this::LoginHandler);
        javalin.delete("/session", this::LogoutHandler);
        javalin.post("/game", this::CreateHandler);
        javalin.put("/game", this::JoinHandler);
        javalin.get("/game", this::ListHandler);
        javalin.delete("/db", this::ClearHandler);
    }

    private void RegisterHandler(Context ctx) {
        var serializer = new Gson();
        UserService userService = this.userService;
//        System.out.println("Register Handler Hit!");
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        if (request.password() == null || request.username() == null || request.email() == null) {
            ctx.status(400);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: bad request")));
            return;
        }
        try {
            RegisterResult result = userService.register(request);
            ctx.status(200);
            ctx.json(serializer.toJson(result));
        } catch(AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: username already taken")));
        } catch(DataAccessException e) {
            ctx.status(404);
            ctx.json(serializer.toJson(new ErrorMessage("message", e.getMessage())));
        }
    }

    private void LoginHandler(Context ctx) {
        var serializer = new Gson();
        UserService userService = this.userService;
//        System.out.println("Login Handler Hit!");
        LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);
        if (request.password() == null || request.username() == null) {
            ctx.status(400);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: bad request")));
            return;
        }
        try {
            LoginResult result = userService.login(request);
            ctx.status(200);
            ctx.json(serializer.toJson(result));
        } catch(DataAccessException e) {
            ctx.status(401);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: unauthorized")));
        }
    }

    private void LogoutHandler(Context ctx) {
        var serializer = new Gson();
        UserService userService = this.userService;
        LogoutRequest request = new LogoutRequest(ctx.header("Authorization"));
        System.out.println();
        if (request.authToken() == null) {
            ctx.status(400);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: bad request")));
            return;
        }
        try {
            LogoutResult result = userService.logout(request);
            ctx.status(200);
            ctx.json(serializer.toJson(result));
        } catch(DataAccessException e) {
            ctx.status(401);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: unauthorized")));
        }
    }

    private void CreateHandler(Context ctx) {
        var serializer = new Gson();
        GameService gameService = this.gameService;
        CreateRequest parser;
        CreateRequest request;

        try {
            String auth = ctx.header("Authorization");
            parser = serializer.fromJson(ctx.body(), CreateRequest.class);
            request = new CreateRequest(auth, parser.gameName());
            if (request.authToken().isBlank() || request.gameName().isBlank()) throw new Exception("empty body or gameName");
        } catch(Exception e) {
            ctx.status(400);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: bad request")));
            return;
        }

        try {
            CreateResult result = gameService.create(request);
            ctx.status(200);
            ctx.json(serializer.toJson(result));
        } catch(DataAccessException e) {
            ctx.status(401);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: unauthorized")));
        }
    }

    private void JoinHandler(Context ctx) {
        var serializer = new Gson();
        GameService gameService = this.gameService;
        JoinRequest parser;
        JoinRequest request;

        try {
            String auth = ctx.header("Authorization");
            parser = serializer.fromJson(ctx.body(), JoinRequest.class);
            request = new JoinRequest(auth, parser.playerColor(), parser.gameID());
            if (request.authToken().isBlank() || request.playerColor() == null || request.gameID().isBlank()) throw new Exception("empty body or gameName");
        } catch(Exception e) {
            ctx.status(400);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: bad request")));
            return;
        }

        try {
            JoinResult result = gameService.join(request);
            ctx.status(200);
            ctx.json(serializer.toJson(result));
        } catch(DataAccessException e) {
            ctx.status(401);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: unauthorized")));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: color already taken")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: bad color choice")));
        }
    }

    private void ListHandler(Context ctx) {
        var serializer = new Gson();
        GameService gameService = this.gameService;
        String auth = ctx.header("Authorization");

        if (auth == null) {
            ctx.status(400);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: bad request")));
            return;
        }

        ListRequest request = new ListRequest(auth);

            try {
                ListResult result = gameService.list(request);
                ctx.status(200);
                ctx.json(serializer.toJson(result));
            } catch (DataAccessException e) {
                ctx.status(401);
                ctx.json(serializer.toJson(new ErrorMessage("message", "Error: unauthorized")));
            }
        }

    private void ClearHandler(Context ctx) throws DataAccessException {
        ClearService clearService = this.clearService;
        clearService.clear();
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
