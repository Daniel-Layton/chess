package service.models;

public record LogoutRequest(
        String authToken,
        String username
) {}
