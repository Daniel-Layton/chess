package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import io.javalin.*;
import io.javalin.http.Context;

import service.ClearService;
import service.GameService;
import service.UserService;
import service.models.ErrorMessage;
import service.models.RegisterRequest;
import service.models.RegisterResult;

public class Server {

    private final Javalin javalin;
    UserService userService = new UserService();
    GameService gameService = new GameService();
    ClearService clearService = new ClearService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::RegisterHandler);
    }

    private void RegisterHandler(Context ctx) {
        var serializer = new Gson();
        UserService userService = new UserService();
        System.out.println("Register Handler Hit!");
        System.out.println(ctx.body());
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        System.out.println(request);
        try {
            RegisterResult result = userService.register(request);
            System.out.println(result.authToken());
            System.out.println(result.username());
            ctx.status(200);
            ctx.json(serializer.toJson(result));
        } catch(AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: username already taken")));
        }
    }

    private void ClearHandler(Context ctx) {
        var serializer = new Gson();
        System.out.println("Register Handler Hit!");
        System.out.println(ctx.body());
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        System.out.println(request);
        try {
            RegisterResult result = userService.register(request);
            System.out.println(result.authToken());
            System.out.println(result.username());
            ctx.status(200);
            ctx.json(serializer.toJson(result));
        } catch(AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(serializer.toJson(new ErrorMessage("message", "Error: username already taken")));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
