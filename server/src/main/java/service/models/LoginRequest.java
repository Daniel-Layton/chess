package service.models;

public record LoginRequest(
        String authToken,
        String username
) {}
