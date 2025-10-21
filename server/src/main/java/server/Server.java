package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import service.UserService;

import java.util.Map;

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
        Map bodyObject = getBodyObject(ctx, Map.class);
        System.out.println(bodyObject);
        System.out.println(bodyObject.get("username"));
        //UserService RegisterResult
        ctx.status(400);
    }

    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
