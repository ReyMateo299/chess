package ui;

import client.ResponseException;
import client.ServerFacade;

import java.util.Scanner;
import java.util.Arrays;

import client.State;
import requests.*;
import results.*;
import static ui.EscapeSequences.*;

public class PreloginUI {
    private final ServerFacade server;
    private State nextState;

    public PreloginUI(ServerFacade server) {
        this.server = server;
    }

    public State run() {
        System.out.println(RESET_TEXT_COLOR + "👑 Welcome to 240 Chess! Type help to get started. 👑");

        Scanner scanner = new Scanner(System.in);
        var result = "";

        nextState = State.PRELOGIN;
        while (nextState == State.PRELOGIN) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        return nextState;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(request);
            nextState = State.POSTLOGIN;
            return "You successfully registered as user: " + result.username();
        }
        throw new ResponseException("Expected form: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String... params) throws ResponseException {
        if (params.length >= 2 ) {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = server.login(request);
            nextState = State.POSTLOGIN;
            return "You successfully logged in as user: " + result.username();
        }
        throw new ResponseException("Expected from: login <USERNAME> <PASSWORD>");
    }

    private String quit() {
        nextState = State.QUIT;
        return "Thanks for playing! Exiting the application...";
    }

    private String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> -> to create an account
                - login <USERNAME> <PASSWORD> -> to play chess
                - quit -> playing chess
                - help -> with possible commands
                """;
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
