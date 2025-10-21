package service.models;

public record LoginRequest(
        String password,
        String username
) {}
