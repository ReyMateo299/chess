package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import client.State;
import requests.*;
import results.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginUI {
    private final ServerFacade server;
    private final Scanner scanner;

    public PostloginUI(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    public UIResult run(String authToken) {
//        System.out.println("\n" + RESET_TEXT_COLOR + "Type help to continue");

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
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
//                case "play" -> joinGame(params);
//                case "observe" -> observeGame(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return new UIResult(ex.getMessage(), State.POSTLOGIN, null);
        }
    }

    private UIResult logout() {
        String message = "Logging out...\n";
        return new UIResult(message, State.PRELOGIN, null);
    }

    private UIResult createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            CreateGameRequest request = new CreateGameRequest(params[0], "gameName");
            CreateGameResult result = server.createGame(request);
            String message = "Successfully created game: " + result.gameID();
            return new UIResult(message, State.POSTLOGIN, null);
        }
        throw new ResponseException("Expected form: create <NAME>");
    }

    private UIResult listGames() throws ResponseException {
        return new UIResult("Message", State.POSTLOGIN, null);
    }

    private UIResult quit() {
        String message = "Thanks for playing! Exiting the application...";
        return new UIResult(message, State.QUIT, null);
    }

    private UIResult help() {
        String message = """
                - create <NAME> -> a game
                - list -> games
                - play <ID> [WHITE|BLACK] -> a game
                - observe <ID> -> a game
                - logout -> when you are done
                - quit -> playing chess
                - help -> with possible commands
                """;
        return new UIResult(message, State.POSTLOGIN, null);
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
