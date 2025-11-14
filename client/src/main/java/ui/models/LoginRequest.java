package ui.models;

public record LoginRequest(
        String password,
        String username
) {}
