package ui;

import chess.ChessGame.TeamColor;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.OpenWebsocket;
import client.ResponseException;
import client.ServerFacade;
import client.State;
import client.websocket.WebSocketFacade;
import model.GameData;
import requests.GetGamesRequest;
import results.GetGamesResult;
import websocket.commands.MakeMoveCommand;

import java.util.*;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private final ServerFacade server;
//    private final WebSocketFacade ws;
    private Scanner scanner;
    private String authToken;
    private WebSocketFacade ws;
    private Integer gameID;
    private TeamColor playerColor;

    public GameplayUI(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    public UIResult run(String authToken, Integer gameID, TeamColor playerColor, WebSocketFacade ws) {
        this.authToken = authToken;
        this.ws = ws;
        this.gameID = gameID;
        this.playerColor = playerColor;

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
                case "highlight" -> highlightMoves(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return new UIResult(ex.getMessage(), State.GAMEPLAY, authToken, null, playerColor);
        }
    }

    private UIResult redrawBoard() throws ResponseException {
        ChessGame game = getChessGame();
        if (game == null) {
            throw new ResponseException("Game not found");
        }
        String message = ChessBoardPrinter.printChessBoard(playerColor, game, null);
        return new UIResult(message, State.GAMEPLAY, authToken, null, playerColor);
    }

    private UIResult leaveGame() throws ResponseException {
        String message = "Leaving game...\n";
        return new UIResult(message, State.POSTLOGIN, authToken, new OpenWebsocket(false, gameID), playerColor);
    }

    private UIResult makeMove(String... params) throws ResponseException {
        if (params.length >= 1 && params[0].length() == 4) {
            String moveInput = params[0];

            Integer startCol;
            Integer endCol;

            try {
                startCol = getCol(moveInput.charAt(0));
                endCol = getCol(moveInput.charAt(2));
            } catch (ResponseException e) {
                throw new ResponseException("Invalid move. Expected form: make <StartPosition><EndPosition>    Example: make e2e4");
            }

            Set<Character> rows = new HashSet<>(Set.of('1', '2', '3', '4', '5', '6', '7', '8'));
            Character startRowChar = moveInput.charAt(1);
            Character endRowChar = moveInput.charAt(3);
            if (!rows.contains(startRowChar) || !rows.contains(endRowChar)) {
                throw new ResponseException("Invalid move. Expected form: make <StartPosition><EndPosition>    Example: make e2e4");
            }
            int startRow = Character.getNumericValue(moveInput.charAt(1));
            int endRow = Character.getNumericValue(moveInput.charAt(3));

            ChessGame game = getChessGame();
            if (game == null) {
                throw new ResponseException("No Game found");
            }

            var startPosition = new ChessPosition(startRow, startCol);
            var endPosition = new ChessPosition(endRow, endCol);
            var startPiece = game.getBoard().getPiece(startPosition);

            if (startPiece == null) {
                throw new ResponseException("Invalid move: No starting piece here.");
            }
            if (startPiece.getTeamColor() != playerColor) {
                throw new ResponseException("Invalid move: It's the other player's turn.");
            }

            ChessPiece.PieceType promotionPiece = null;

            if ((endRow == 1 && playerColor == TeamColor.BLACK) ||
                    (endRow == 8 && playerColor == TeamColor.WHITE)) {
                Set<String> validPromotions = new HashSet<>(Set.of("N", "B", "R", "Q"));

                boolean validResponse = false;
                while (!validResponse) {
                    Scanner secondScanner = new Scanner(System.in);
                    System.out.print("\n" + RESET_TEXT_COLOR + "Options: N, B, R, Q");
                    System.out.print("\n" + "[IN_GAME] Enter Promotion piece type >>> " + SET_TEXT_COLOR_GREEN);
                    String line = secondScanner.nextLine();
                    String[] tokens = line.toLowerCase().split(" ");
                    String pieceString = tokens[0];
                    if (tokens.length == 1 && validPromotions.contains(pieceString)) {
                        validResponse = true;
                        switch (pieceString) {
                            case "N" -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                            case "B" -> promotionPiece = ChessPiece.PieceType.BISHOP;
                            case "R" -> promotionPiece = ChessPiece.PieceType.ROOK;
                            case "Q" -> promotionPiece = ChessPiece.PieceType.QUEEN;
                        }
                    }
                }
            }

            var proposedMove = new ChessMove(startPosition, endPosition, promotionPiece);
            if (!game.validMoves(startPosition).contains(proposedMove)) {
                throw new ResponseException("Invalid move");
            }

            ws.sendCommand(new MakeMoveCommand(authToken, gameID, proposedMove));
            return new UIResult("Attempting to make move...", State.GAMEPLAY, authToken, null, playerColor);
        }
        throw new ResponseException("Expected form: make <StartPosition><EndPosition>    Example: make e2e4");
    }

    private UIResult resign() throws ResponseException {
        String message = "resign\n";
        return new UIResult(message, State.GAMEPLAY, authToken, null, playerColor);
    }

    private UIResult highlightMoves(String... params) throws ResponseException {
        if (params.length >= 1 && params[0].length() == 2) {
            String positionInput = params[0];
            Integer startCol;
            try {
                startCol = getCol(positionInput.charAt(0));
            } catch (ResponseException e) {
                throw new ResponseException("Expected form: make <StartPosition><EndPosition>    Example: make e2e4");
            }

            Set<Character> rows = new HashSet<>(Set.of('1', '2', '3', '4', '5', '6', '7', '8'));
            Character startRowChar = positionInput.charAt(1);
            if (!rows.contains(startRowChar)) {
                throw new ResponseException("Invalid position. Expected form: highlight <StartPosition>    Example: highlight e2");
            }
            int startRow = Character.getNumericValue(positionInput.charAt(1));

            var startPosition = new ChessPosition(startRow, startCol);

            ChessGame game = getChessGame();
            if (game == null) {
                throw new ResponseException("Game not found");
            }
            String message = ChessBoardPrinter.printChessBoard(playerColor, game, startPosition);
            return new UIResult(message, State.GAMEPLAY, authToken, null, playerColor);
        }
        throw new ResponseException("Expected form: highlight <StartPosition>    Example: highlight e2");
    }

    private Integer getCol(Character colChar) throws ResponseException {
        Map<Character, Integer> cols;
        if (playerColor == TeamColor.WHITE) {
            cols = new HashMap<>(Map.of(
                    'a', 1,
                    'b', 2,
                    'c', 3,
                    'd', 4,
                    'e', 5,
                    'f', 6,
                    'g', 7,
                    'h', 8));
        } else {
            cols = new HashMap<>(Map.of(
                    'a', 8,
                    'b', 7,
                    'c', 6,
                    'd', 5,
                    'e', 4,
                    'f', 3,
                    'g', 2,
                    'h', 1));
        }
        if (!cols.containsKey(colChar)) {
            throw new ResponseException("getCol Failed");
        }
        return cols.get(colChar);
    }

    private ChessGame getChessGame() throws ResponseException {
        GetGamesResult result = server.getGames(new GetGamesRequest(authToken));
        ChessGame game = null;
        for (GameData gameData : result.games()) {
            if (gameData.gameID() == gameID) {
                game = gameData.game();
            }
        }
        return game;
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
        return new UIResult(message, State.GAMEPLAY, null, null, playerColor);
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
