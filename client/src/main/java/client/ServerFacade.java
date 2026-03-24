package client;

import requests.*;
import results.*;

import java.net.http.HttpClient;

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
}
