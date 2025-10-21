package service.models;

public record RegisterRequest(
        String authToken,
        String username
) {}
