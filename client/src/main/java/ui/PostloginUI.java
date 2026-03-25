package ui;

import client.ResponseException;
import client.ServerFacade;
import client.State;
import requests.*;
import results.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginUI {
    private final ServerFacade server;
    private final Scanner scanner;
    private String authToken;
    private ArrayList<GameResult> listedGames;

    public PostloginUI(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
        this.listedGames = new ArrayList<>();
    }

    public UIResult run(String authToken) {
//        System.out.println("\n" + RESET_TEXT_COLOR + "Type help to continue");
        this.authToken = authToken;

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
                case "play" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return new UIResult(ex.getMessage(), State.POSTLOGIN, authToken);
        }
    }

    private UIResult logout() throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        String message = "Logging out...";
        return new UIResult(message, State.PRELOGIN, null);
    }

    private UIResult createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            CreateGameRequest request = new CreateGameRequest(authToken, params[0]);
            CreateGameResult result = server.createGame(request);
            String message = "Successfully created game: " + result.gameID();
            return new UIResult(message, State.POSTLOGIN, authToken);
        }
        throw new ResponseException("Expected form: create <NAME>");
    }

    private UIResult listGames() throws ResponseException {
        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = server.listGames(request);

        ArrayList<GameResult> newListedGames = new ArrayList<>();
        StringBuilder message = new StringBuilder();
        int i = 1;
        for (GameResult game : result.games()) {
            String gameString = "(" + i +
                    ") Game: " + game.gameName() +
                    " White: " + game.whiteUsername() +
                    " Black: " + game.blackUsername() + "\n";
            message.append(gameString);
            newListedGames.add(game);
            i++;
        }
        message.deleteCharAt(message.length() - 1);
        listedGames = newListedGames;
        return new UIResult(message.toString(), State.POSTLOGIN, authToken);
    }

    private UIResult joinGame(String... params) throws ResponseException {
        if (params.length >= 2 && (params[1].equals("white") || params[1].equals("black"))) {
            int gameID = 0;
            try {
                gameID = Integer.parseInt(params[0]) - 1;
            } catch (NumberFormatException e) {
                throw new ResponseException(" Invalid input: " + params[0] + " - <ID> must be an integer number.");
            }
            if (gameID <= -1 || gameID >= listedGames.size()) {
                throw new ResponseException("Invalid input: " + params[0] + " - Game not found.");
            }

            JoinGameRequest request = new JoinGameRequest(authToken, params[1].toUpperCase(), gameID);
            server.joinGame(request);
            String message = "Successfully joined game: " + params[0] + "\n" +
                    ChessBoardPrinter.printChessBoard(params[1].toUpperCase());
            return new UIResult(message, State.POSTLOGIN, authToken); // DO: Change State to GAMEPLAY
        }
        throw new ResponseException("Expected form: play <ID> [WHITE|BLACK]");
    }

    private UIResult observeGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            int gameID = 0;
            try {
                gameID = Integer.parseInt(params[0]) - 1;
            } catch (NumberFormatException e) {
                throw new ResponseException(" Invalid input: " + params[0] + " - <ID> must be an integer number.");
            }
            if (gameID <= -1 || gameID >= listedGames.size()) {
                throw new ResponseException("Invalid input: " + params[0] + " - Game not found.");
            }

            String message = "Successfully joined game <" + params[0] + "> as an observer." + "\n" +
                    ChessBoardPrinter.printChessBoard("WHITE");
            return new UIResult(message, State.POSTLOGIN, authToken); // DO: Change State to GAMEPLAY
        }
        throw new ResponseException("Expected form: observe <ID>");
    }

    private UIResult quit() throws ResponseException {
        server.logout(new LogoutRequest(authToken));
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
                - help -> with possible commands""";
        return new UIResult(message, State.POSTLOGIN, authToken);
    }

    private void printPrompt() {
        System.out.print("\n\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
