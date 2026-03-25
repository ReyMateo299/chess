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
    private final Scanner scanner;

    public PreloginUI(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    public UIResult run() {
        printPrompt();
        String line = scanner.nextLine();
        UIResult uiResult = eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + uiResult.message());
        return uiResult;
    }

    public UIResult eval(String input) {
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
            return new UIResult(ex.getMessage(), State.PRELOGIN, null);
        }
    }

    private UIResult register(String... params) throws ResponseException {
        if (params.length >= 3) {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(request);
            String message = "You successfully registered as user: " + result.username() + ". Type help to continue.";
            return new UIResult(message, State.POSTLOGIN, result.authToken());
        }
        throw new ResponseException("Expected form: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    private UIResult login(String... params) throws ResponseException {
        if (params.length >= 2 ) {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = server.login(request);
            String message = "You successfully logged in as user: " + result.username() + ". Type help to continue.";
            return new UIResult(message, State.POSTLOGIN, result.authToken());
        }
        throw new ResponseException("Expected from: login <USERNAME> <PASSWORD>");
    }

    private UIResult quit() {
        String message = "Thanks for playing! Exiting the application...";
        return new UIResult(message, State.QUIT, null);
    }

    private UIResult help() {
        String message =  """
                - register <USERNAME> <PASSWORD> <EMAIL> -> to create an account
                - login <USERNAME> <PASSWORD> -> to play chess
                - quit -> playing chess
                - help -> with possible commands""";
        return new UIResult(message, State.PRELOGIN, null);
    }

    private void printPrompt() {
        System.out.print("\n\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
