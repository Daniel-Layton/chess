package ui.models;

public record CreateRequest(
        String authToken,
        String gameName
) {}
