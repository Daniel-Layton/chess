package service.models;

public record LoginResult(
        String authToken,
        String username
) {}
