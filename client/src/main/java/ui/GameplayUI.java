package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.OpenWebsocket;
import client.ResponseException;
import client.ServerFacade;
import client.State;
import client.websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private final ServerFacade server;
//    private final WebSocketFacade ws;
    private Scanner scanner;
    private String authToken;
    private WebSocketFacade ws;
    private Integer gameID;

    public GameplayUI(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    public UIResult run(String authToken, Integer gameID, WebSocketFacade ws) {
        this.authToken = authToken;
        this.ws = ws;
        this.gameID = gameID;

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
                case "make" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlightMoves();
                default -> help();
            };
        } catch (ResponseException ex) {
            return new UIResult(ex.getMessage(), State.GAMEPLAY, authToken, null);
        }
    }

    private UIResult redrawBoard() throws ResponseException {
        String message = "redraw board\n";
        return new UIResult(message, State.GAMEPLAY, authToken, null);
    }

    private UIResult leaveGame() throws ResponseException {
        String message = "Leaving game...\n";
        return new UIResult(message, State.POSTLOGIN, authToken, new OpenWebsocket(false, 0));
    }

    private UIResult makeMove(String... params) throws ResponseException {
        String message = "make move\n";
        ws.sendCommand(new MakeMoveCommand(
                authToken, gameID, new ChessMove(
                        new ChessPosition(1, 1), new ChessPosition(1, 1), ChessPiece.PieceType.QUEEN)
        ));
        return new UIResult(message, State.GAMEPLAY, authToken, null);
    }

    private UIResult resign() throws ResponseException {
        String message = "resign\n";
        return new UIResult(message, State.GAMEPLAY, authToken, null);
    }

    private UIResult highlightMoves() throws ResponseException {
        String message = "highlight\n";
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
        System.out.print("\n" + RESET_TEXT_COLOR + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
