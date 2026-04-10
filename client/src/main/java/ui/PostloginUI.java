package ui;

import chess.ChessGame;
import client.OpenWebsocket;
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
    private ChessGame.TeamColor playerColor;

    public PostloginUI(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
        this.listedGames = new ArrayList<>();
    }

    public UIResult run(String authToken) {
//        System.out.println("\n" + RESET_TEXT_COLOR + "Type help to continue");
        this.authToken = authToken;
        this.playerColor = null;

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
            return new UIResult(ex.getMessage(), State.POSTLOGIN, authToken, null, null);
        }
    }

    private UIResult logout() throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        String message = "Logging out...\n";
        return new UIResult(message, State.PRELOGIN, null, null, null);
    }

    private UIResult createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            CreateGameRequest request = new CreateGameRequest(authToken, params[0]);
            CreateGameResult result = server.createGame(request);
            String message = "Successfully created game: " + result.gameID() + "\n";
            return new UIResult(message, State.POSTLOGIN, authToken, null, null);
        }
        throw new ResponseException("Expected form: create <NAME>\n");
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

        listedGames = newListedGames;

        if (listedGames.isEmpty()) {
            return new UIResult("No existing games.\n", State.POSTLOGIN, authToken, null, null);
        }

        return new UIResult(message.toString(), State.POSTLOGIN, authToken, null, null);
    }

    private UIResult joinGame(String... params) throws ResponseException {
        if (params.length >= 2 && (params[1].equals("white") || params[1].equals("black"))) {
            int gameID = 0;
            try {
                gameID = Integer.parseInt(params[0]) - 1;
            } catch (NumberFormatException e) {
                throw new ResponseException(" Invalid input: " + params[0] + " - <ID> must be an integer number.\n");
            }
            if (gameID <= -1 || gameID >= listedGames.size()) {
                throw new ResponseException("Invalid input: " + params[0] + " - Game not found.\n");
            }

            int realGameID = listedGames.get(gameID).gameID();
            JoinGameRequest request = new JoinGameRequest(authToken, params[1].toUpperCase(), realGameID);
            server.joinGame(request);
            if (params[1].equals("white")) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else {
                playerColor = ChessGame.TeamColor.BLACK;
            }

            String message = "Joining game <" + params[0] + "> as a player ...\n";
            return new UIResult(message, State.GAMEPLAY, authToken, new OpenWebsocket(true, realGameID), playerColor);
        }
        throw new ResponseException("Expected form: play <ID> [WHITE|BLACK]\n");
    }

    private UIResult observeGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            int gameID = 0;
            try {
                gameID = Integer.parseInt(params[0]) - 1;
            } catch (NumberFormatException e) {
                throw new ResponseException(" Invalid input: " + params[0] + " - <ID> must be an integer number.\n");
            }
            if (gameID <= -1 || gameID >= listedGames.size()) {
                throw new ResponseException("Invalid input: " + params[0] + " - Game not found.\n");
            }

            int realGameID = listedGames.get(gameID).gameID();
            String message = "Joining game <" + params[0] + "> as an observer ...\n";
            return new UIResult(message, State.GAMEPLAY, authToken, new OpenWebsocket(true, realGameID), null);
        }
        throw new ResponseException("Expected form: observe <ID>\n");
    }

    private UIResult quit() throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        String message = "Thanks for playing! Exiting the application...\n";
        return new UIResult(message, State.QUIT, null, null, null);
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
        return new UIResult(message, State.POSTLOGIN, authToken, null, null);
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
