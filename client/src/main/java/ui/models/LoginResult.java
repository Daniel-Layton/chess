package ui.models;

public record LoginResult(
        String authToken,
        String username
) {}
