package ui;

import chess.*;
import chess.ChessGame.TeamColor;

import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.*;

public class ChessBoardPrinter {

    public static String printChessBoard(TeamColor color, ChessGame game, ChessPosition startPosition) {
        Collection<ChessPosition> greenMoves = new HashSet<>();

        if (startPosition != null && game.getBoard().getPiece(startPosition) != null) {
            for (ChessMove move : game.validMoves(startPosition)) {
                greenMoves.add(move.getEndPosition());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(printLetterRow(color)).append(nextLine());

        ChessBoard board = game.getBoard();
        if (color == null || color == TeamColor.WHITE) {
            sb.append(printCheckersWhite(board, startPosition, greenMoves));
        } else {
            sb.append(printCheckersBlack(board, startPosition, greenMoves));
        }

        sb.append(printLetterRow(color)).append(nextLine());
        return sb.toString();
    }

    private static String printCheckersWhite(ChessBoard board, ChessPosition startPosition, Collection<ChessPosition> greenMoves) {
        StringBuilder sb = new StringBuilder();

        for (int i = 8; i > 0; i--) {
            sb.append(setBorderConfigs());
            sb.append(" ").append(i).append(" ");
            if (i % 2 == 0) {
                sb.append(printRowWhite(TeamColor.WHITE, i, board, startPosition, greenMoves));
            } else {
                sb.append(printRowWhite(TeamColor.BLACK, i, board, startPosition, greenMoves));
            }
            sb.append(setBorderConfigs());
            sb.append(" ").append(i).append(" ");
            sb.append(nextLine());
        }
        return sb.toString();
    }

    private static String printCheckersBlack(ChessBoard board, ChessPosition startPosition, Collection<ChessPosition> greenMoves) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < 9; i++) {
            sb.append(setBorderConfigs());
            sb.append(" ").append(i).append(" ");
            if (i % 2 == 1) {
                sb.append(printRowBlack(TeamColor.WHITE, i, board, startPosition, greenMoves));
            } else {
                sb.append(printRowBlack(TeamColor.BLACK, i, board, startPosition, greenMoves));
            }
            sb.append(setBorderConfigs());
            sb.append(" ").append(i).append(" ");
            sb.append(nextLine());
        }
        return sb.toString();
    }

    private static String printRowWhite(
            TeamColor startingTile,
            Integer i,
            ChessBoard board,
            ChessPosition startPosition,
            Collection<ChessPosition> greenMoves) {
        StringBuilder sb = new StringBuilder();

        TeamColor tileColor = startingTile;
        for (int j = 1; j < 9; j++) {
            ChessPosition square = new ChessPosition(i, j);
            sb.append(printSquare(square, tileColor, startPosition, greenMoves, board));
            tileColor = swapTile(tileColor);
        }

        return sb.toString();
    }

    private static String printRowBlack(
            TeamColor startingTile,
            Integer i,
            ChessBoard board,
            ChessPosition startPosition,
            Collection<ChessPosition> greenMoves) {
        StringBuilder sb = new StringBuilder();

        TeamColor tileColor = startingTile;
        for (int j = 8; j > 0; j--) {
            ChessPosition square = new ChessPosition(i, j);
            sb.append(printSquare(square, tileColor, startPosition, greenMoves, board));
            tileColor = swapTile(tileColor);
        }

        return sb.toString();
    }

    private static String printSquare(ChessPosition position, TeamColor tileColor,
                                      ChessPosition startPosition, Collection<ChessPosition> greenMoves, ChessBoard board) {
        StringBuilder sb = new StringBuilder();

        if (position.equals(startPosition)) {
            sb.append(SET_BG_COLOR_YELLOW);
        } else if (!greenMoves.isEmpty() && greenMoves.contains(position)) {
            if (tileColor == TeamColor.WHITE) {
                sb.append(SET_BG_COLOR_GREEN);
            } else {
                sb.append(SET_BG_COLOR_DARK_GREEN);
            }
        } else {
            if (tileColor == TeamColor.WHITE) {
                sb.append(SET_BG_COLOR_WHITE);
            } else {
                sb.append(SET_BG_COLOR_BLACK);
            }
        }
        ChessPiece piece = board.getPiece(position);

        if (piece == null) {
            sb.append("   ");
            return sb.toString();
        }

        if (piece.getTeamColor() == TeamColor.WHITE) {
            sb.append(SET_TEXT_COLOR_RED);
        } else {
            sb.append(SET_TEXT_COLOR_BLUE);
        }
        switch (piece.getPieceType()) {
            case PAWN -> sb.append(" P ");
            case ROOK -> sb.append(" R ");
            case KNIGHT -> sb.append(" N ");
            case BISHOP -> sb.append(" B ");
            case QUEEN -> sb.append(" Q ");
            case KING -> sb.append(" K ");
        }

        return sb.toString();
    }

    private static String printLetterRow(TeamColor color) {
        if (color == TeamColor.WHITE) {
            return setBorderConfigs() + "    a  b  c  d  e  f  g  h    ";
        }
        return setBorderConfigs() + "    h  g  f  e  d  c  b  a    ";
    }

    private static TeamColor swapTile(TeamColor tileColor) {
        if (tileColor == TeamColor.BLACK) {
            return TeamColor.WHITE;
        } else {
            return TeamColor.BLACK;
        }
    }

    private static String nextLine() {
        return RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
    }

    public static String setBorderConfigs() {
        return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK;
    }
}
