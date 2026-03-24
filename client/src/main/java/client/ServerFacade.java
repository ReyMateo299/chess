package client;

import requests.RegisterRequest;
import results.RegisterResult;

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
}
