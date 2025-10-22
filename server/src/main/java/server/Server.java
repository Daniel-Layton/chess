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
        javalin.delete("/db", this::ClearHandler);
    }

    private void RegisterHandler(Context ctx) {
        var serializer = new Gson();
        UserService userService = new UserService();
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
        }
    }

    private void LoginHandler(Context ctx) {
        var serializer = new Gson();
        UserService userService = new UserService();
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
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: unaauthorized")));
        }
    }

    private void ClearHandler(Context ctx) {
        var serializer = new Gson();
//        System.out.println("Clear Handler Hit!");
        ClearService clearService = new ClearService();
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
