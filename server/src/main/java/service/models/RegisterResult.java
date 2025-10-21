package service.models;

public record RegisterResult(
        String authToken,
        String username
) {}