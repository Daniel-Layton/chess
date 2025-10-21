package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import service.UserService;
import service.models.RegisterRequest;
import service.models.RegisterResult;

import java.util.Map;

public class Server {

    private final Javalin javalin;

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
        RegisterResult result = userService.register(request);
        ctx.status(400);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
