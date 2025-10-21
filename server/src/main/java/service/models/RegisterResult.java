package service.models;

public record RegisterResult(
        String username,
        String authToken
) {}