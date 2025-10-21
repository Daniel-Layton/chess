package server;

import io.javalin.*;
import io.javalin.http.Context;

import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::RegisterHandler);
    }

    private void RegisterHandler(Context ctx) {
        System.out.println("Register Handler Hit!");
        System.out.println(ctx.body());
        UserService RegisterResult
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
