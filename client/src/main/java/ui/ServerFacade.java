package ui;

import com.google.gson.Gson;
import ui.models.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest register_request) throws Exception {
        var request = buildRequest("POST", "/user", register_request);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest login_request) throws Exception {
        var request = buildRequest("POST", "/session", login_request);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logout_request) throws Exception {
        var request = buildHeaderRequest("DELETE", "/session", null, logout_request.authToken());
        var response = sendRequest(request);
        return handleResponse(response, LogoutResult.class);
    }

    public CreateResult create(CreateRequest create_request) throws Exception {
        var request = buildHeaderRequest("POST", "/game", create_request, create_request.authToken());
        var response = sendRequest(request);
        return handleResponse(response, CreateResult.class);
    }

    public ListResult list(ListRequest list_request) throws Exception {
        var request = buildHeaderRequest("GET", "/game", null, list_request.authToken());
        var response = sendRequest(request);
        return handleResponse(response, ListResult.class);
    }

    public JoinResult join(JoinRequest join_request) throws Exception {
        var request = buildHeaderRequest("PUT", "/game", join_request, join_request.authToken());
        var response = sendRequest(request);
        return handleResponse(response, JoinResult.class);
    }

    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest buildHeaderRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .header("Authorization", authToken)
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        return client.send(request, BodyHandlers.ofString());
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new Exception();
            }
            throw new Exception();
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}