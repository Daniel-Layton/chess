package service.models;

public record RegisterRequest(
        String username,
        String password,
        String email
) {}
