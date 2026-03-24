package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import client.State;
import requests.CreateGameRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.RegisterResult;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginUI {
    private final ServerFacade server;
    private State nextState;

    public PostloginUI(ServerFacade server) {
        this.server = server;
    }

    public State run() {
        System.out.println("\n" + RESET_TEXT_COLOR + "Type help to continue");

        Scanner scanner = new Scanner(System.in);
        var result = "";

        nextState = State.POSTLOGIN;
        while (nextState == State.POSTLOGIN) {
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
                case "logout" -> logout();
                case "create" -> createGame(params);
//                case "list" -> listGames();
//                case "play" -> joinGame(params);
//                case "observe" -> observeGame(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String logout() {
        nextState = State.PRELOGIN;
        return "Logging out...\n";
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            CreateGameRequest request = new CreateGameRequest(params[0], params[1]);
//            CreateGameResult result = server.createGame(request);
//            return "Successfully created game: " + result.ID();
        }
        throw new ResponseException("Expected form: create <NAME>");
    }

    private String quit() {
        nextState = State.QUIT;
        return "Thanks for playing! Exiting the application...";
    }

    private String help() {
        return """
                - create <NAME> -> a game
                - list -> games
                - play <ID> [WHITE|BLACK] -> a game
                - observe <ID> -> a game
                - logout -> when you are done
                - quit -> playing chess
                - help -> with possible commands
                """;
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
