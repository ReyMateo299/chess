package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import client.State;
import model.GameData;
import requests.*;
import results.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        String message = "Logging out...\n";
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
            String message = "Successfully joined game: " + params[0] + "\n" + printChessBoard(params[1].toUpperCase());
            return new UIResult(message, State.POSTLOGIN, authToken); // TODO: Change State to GAMEPLAY
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

            String message = "Successfully joined game <" + params[0] + "> as an observer." + "\n" + printChessBoard("WHITE");
            return new UIResult(message, State.POSTLOGIN, authToken); // TODO: Change State to GAMEPLAY
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

    private String printChessBoard(String color) {
        StringBuilder sb = new StringBuilder();

        sb.append(printLetterRow(color));
        sb.append(nextLine());
        if (color.equals("WHITE")) {
            sb.append(printCheckersWhite());
        } else {
            sb.append(printCheckersBlack());
        }

        sb.append(printLetterRow(color));
        sb.append(nextLine());

        return sb.toString();
    }

    private String printCheckersWhite() {
        StringBuilder sb = new StringBuilder();

        sb.append(SET_BORDER_CONFIGS).append(" 8 ");
        sb.append(SET_TEXT_COLOR_BLUE).append(printLastRowCheckers("WHITE"));
        sb.append(SET_BORDER_CONFIGS).append(" 8 ").append(nextLine());

        sb.append(SET_BORDER_CONFIGS).append(" 7 ");
        sb.append(SET_TEXT_COLOR_BLUE).append(printPawns("BLACK"));
        sb.append(SET_BORDER_CONFIGS).append(" 7 ").append(nextLine());

        int i = 0;
        String[] rows = {" 6 ", " 5 ", " 4 ", " 3 "};
        String currTile = "WHITE";
        while (i < 4) {
            sb.append(SET_BORDER_CONFIGS).append(rows[i]);
            sb.append(printEmptyRow(currTile));
            sb.append(SET_BORDER_CONFIGS).append(rows[i]).append(nextLine());
            currTile = swapTile(currTile);
            i++;
        }

        sb.append(SET_BORDER_CONFIGS).append(" 2 ");
        sb.append(SET_TEXT_COLOR_RED).append(printPawns("WHITE"));
        sb.append(SET_BORDER_CONFIGS).append(" 2 ").append(nextLine());

        sb.append(SET_BORDER_CONFIGS).append(" 1 ");
        sb.append(SET_TEXT_COLOR_RED).append(printLastRowCheckers("BLACK"));
        sb.append(SET_BORDER_CONFIGS).append(" 1 ").append(nextLine());

        return sb.toString();
    }

    private String printCheckersBlack() {
        return "HELLO";
    }

    private String printLastRowCheckers(String startingTile) {
        String currTile = startingTile;
        StringBuilder result = new StringBuilder();
        String[] pieces = {"R", "N", "B", "Q", "K", "B", "N", "R"};
        int i = 0;

        while (i < 8) {
            if (currTile.equals("BLACK")) {
                result.append(SET_BG_COLOR_BLACK);
                result.append(pieces[i]);
                currTile = "WHITE";
            } else {
                result.append(SET_BG_COLOR_WHITE);
                result.append(pieces[i]);
                currTile = "BLACK";
            }
            i++;
        }
        return result.toString();
    }

    private String printLetterRow(String color) {
        if (color.equals("WHITE")) {
            return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    ";
        }
        return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    ";
    }

    private String printPawns(String startingTile) {
        String currTile = startingTile;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < 8) {
            if (currTile.equals("BLACK")) {
                result.append(SET_BG_COLOR_BLACK + " P ");
                currTile = "WHITE";
            } else {
                result.append(SET_BG_COLOR_WHITE + " P ");
                currTile = "BLACK";
            }
            i++;
        }
        return result.toString();
    }

    private String printEmptyRow(String startingTile) {
        String currTile = startingTile;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < 8) {
            if (currTile.equals("BLACK")) {
                result.append(SET_BG_COLOR_BLACK + "   ");
                currTile = "WHITE";
            } else {
                result.append(SET_BG_COLOR_WHITE + "   ");
                currTile = "BLACK";
            }
            i++;
        }
        return result.toString();
    }

    private String swapTile(String currTile) {
        if (currTile.equals("BLACK")) {
            return "WHITE";
        } else {
            return "BLACK";
        }
    }

    private String nextLine() {
        return RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
    }
}
