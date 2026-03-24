package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import client.State;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginUI implements UI {
    private final ServerFacade server;
    private State nextState;

    public PostloginUI(ServerFacade server) {
        this.server = server;
        this.nextState = State.POSTLOGIN;
    }

    public State run() {
        System.out.println("\n" + RESET_TEXT_COLOR + "Type help to continue");

        Scanner scanner = new Scanner(System.in);
        var result = "";
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
//                case "logout" -> logout();
//                case "create" -> createGame(params);
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
