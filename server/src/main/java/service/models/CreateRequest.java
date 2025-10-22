package service.models;

import server.Server;

public record CreateRequest(
        String authToken,
        String gameName
) {}
