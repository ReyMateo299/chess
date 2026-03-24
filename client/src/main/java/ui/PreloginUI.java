package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;

import java.util.Scanner;
import java.util.Arrays;

import requests.*;
import results.*;
import static ui.EscapeSequences.*;

public class PreloginUI implements UI {
    private final ServerFacade server;

    public PreloginUI(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
//                case "login" -> doStuff(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }

//        if (input.equals("quit")) {
//            return "quit";
//        }
//        return "Hello World";
    }

    private String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(request);
            return "You successfully registered as user: " + result.username();
        }
        throw new ResponseException("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }
}
