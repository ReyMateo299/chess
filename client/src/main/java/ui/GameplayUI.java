package ui;

import client.ResponseException;
import client.ServerFacade;
import client.State;
import client.websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private final ServerFacade server;
//    private final WebSocketFacade ws;
    private Scanner scanner;
    private String authToken;

    public GameplayUI(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    public UIResult run(String authToken) {
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
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "make" -> makeMove();
                case "resign" -> resign();
                case "highlight" -> highlightMoves();
                default -> help();
            };
        } catch (ResponseException ex) {
            return new UIResult(ex.getMessage(), State.POSTLOGIN, authToken, null);
        }
    }

    private UIResult redrawBoard() throws ResponseException {
        String message = "";
        return new UIResult(message, State.GAMEPLAY, authToken, null);
    }

    private UIResult leaveGame() throws ResponseException {
        String message = "";
        return new UIResult(message, State.POSTLOGIN, authToken, null);
    }

    private UIResult makeMove() throws ResponseException {
        String message = "";
        return new UIResult(message, State.GAMEPLAY, authToken, null);
    }

    private UIResult resign() throws ResponseException {
        String message = "";
        return new UIResult(message, State.GAMEPLAY, authToken, null);
    }

    private UIResult highlightMoves() throws ResponseException {
        String message = "";
        return new UIResult(message, State.GAMEPLAY, authToken, null);
    }

    private UIResult help() {
        String message =  """
                - redraw -> the chess board
                - leave -> current game
                - make <MOVE> -> a move
                - resign -> the game
                - highlight -> legal moves
                - help -> with possible commands
                """;
        return new UIResult(message, State.GAMEPLAY, null, null);
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
