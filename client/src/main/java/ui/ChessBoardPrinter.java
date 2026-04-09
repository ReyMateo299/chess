package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessGame.TeamColor;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class ChessBoardPrinter {

    public static String printChessBoard(TeamColor color, ChessBoard board) {
        StringBuilder sb = new StringBuilder();
        sb.append(printLetterRow(color)).append(nextLine());

        if (color == TeamColor.WHITE) {
            sb.append(printCheckersWhite(board));
        } else {
            sb.append(printCheckersBlack(board));
        }

        sb.append(printLetterRow(color)).append(nextLine());
        return sb.toString();
    }

    private static String printCheckersWhite(ChessBoard board) {
        StringBuilder sb = new StringBuilder();

        for (int i = 8; i > 0; i--) {
            if (i % 2 == 0) {
                sb.append(printRow(TeamColor.WHITE, i, board));
            } else {
                sb.append(printRow(TeamColor.BLACK, i, board));
            }
            sb.append(nextLine());
        }
        return sb.toString();
    }

    private static String printCheckersBlack(ChessBoard board) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < 9; i++) {
            if (i % 2 == 1) {
                sb.append(printRow(TeamColor.WHITE, i, board));
            } else {
                sb.append(printRow(TeamColor.BLACK, i, board));
            }
            sb.append(nextLine());
        }
        return sb.toString();
    }

    private static String printRow(TeamColor startingTile, Integer i, ChessBoard board) {
        StringBuilder sb = new StringBuilder();

        sb.append(setBorderConfigs());
        sb.append(" ").append(i).append(" ");
        sb.append(SET_TEXT_COLOR_BLUE);

        TeamColor tileColor = startingTile;
        for (int j = 1; j < 9; j++) {

            if (tileColor == TeamColor.WHITE) {
                sb.append(SET_BG_COLOR_WHITE);
            } else {
                sb.append(SET_BG_COLOR_BLACK);
            }

            sb.append(printSquare(board.getPiece(new ChessPosition(i, j))));
            tileColor = swapTile(tileColor);
        }

        sb.append(setBorderConfigs());
        sb.append(" ").append(i).append(" ");

        return sb.toString();
    }

    private static String printSquare(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        StringBuilder sb = new StringBuilder();
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
