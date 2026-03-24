package client;

import requests.*;
import results.*;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    // Build request/result instances here

    public RegisterResult register(RegisterRequest request) {
        return new RegisterResult(request.username(), "testAuth");
    }

    public LoginResult login(LoginRequest request) {
        return new LoginResult(request.username(), "testAuth");
    }

    public void logout(LogoutRequest request) {

    }

    public ListGamesResult listGames(ListGamesRequest request) {
        return new ListGamesResult(List.of(new GameResult(
                1, "white", "black", "gameName"
                ))
        );
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        return new CreateGameResult(1);
    }

    public void joinGame(JoinGameRequest request) {

    }

    public void clear() {

    }
}
