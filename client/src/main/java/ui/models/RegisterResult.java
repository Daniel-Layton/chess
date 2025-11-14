package ui.models;

public record RegisterResult(
        String username,
        String authToken
) {}