package client;

import requests.*;
import results.*;

import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    // Build request/result instances here

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        var httpRequest = buildRequest("POST", path, request);
        var response = sendRequest(httpRequest);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        var path = "/session";
        var httpRequest = buildRequest("POST", path, request);
        var response = sendRequest(httpRequest);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest request) throws ResponseException {

    }

    public ListGamesResult listGames(ListGamesRequest request) throws ResponseException {
        return new ListGamesResult(List.of(new GameResult(
                1, "white", "black", "gameName"
                ))
        );
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        return new CreateGameResult(1);
    }

    public void joinGame(JoinGameRequest request) throws ResponseException {

    }

    public void clear() throws ResponseException {
        var path = "/db";
        var httpRequest = buildRequest("DELETE", path, null);
        sendRequest(httpRequest);
//        return handleResponse(response, RegisterResult.class);
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

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                var map = new Gson().fromJson(body, HashMap.class);
                String message = map.get("message").toString();
                throw new ResponseException(message);
            }
            throw new ResponseException("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

//    public static String fromHttpStatusCode(int httpStatusCode) {
//        return switch (httpStatusCode) {
//            case 500 -> "ServerError";
//            case 400 -> "ClientError";
//            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
//        };
//    }
}
