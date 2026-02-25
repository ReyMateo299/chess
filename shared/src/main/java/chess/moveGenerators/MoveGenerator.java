package chess.moveGenerators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;
import java.util.Set;

public class MoveGenerator {

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final int myRow;
    private final int myCol;

    MoveGenerator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.myRow = myPosition.getRow();
        this.myCol = myPosition.getColumn();
    }

    Set<ChessMove> generateSingle(int[][] directions) {
        Set<ChessMove> moves = new HashSet<>();

        for (int[] direction: directions) {
            if (isOpen(myRow + direction[0], myCol + direction[1]) || isEnemy(myRow + direction[0], myCol + direction[1])) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myRow + direction[0], myCol + direction[1]), null));
            }
        }

        return moves;
    }

    Set<ChessMove> generateMany(int[][] directions) {
        Set<ChessMove> moves = new HashSet<>();

        for (int[] direction: directions) {
            int row = myRow;
            int col = myCol;
            while (isOpen(row + direction[0], col + direction[1])) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + direction[0], col + direction[1]), null));
                row = row + direction[0];
                col = col + direction[1];
            }
            if (isEnemy(row + direction[0], col + direction[1])) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + direction[0], col + direction[1]), null));
            }
        }

        return moves;
    }

    public boolean isEnemy(int row, int col) {
        if (offBoard(row, col)) {
            return false;
        } else if (!isOpen(row, col)) {
            return board.getPiece(new ChessPosition(row, col)).getTeamColor() != board.getPiece(myPosition).getTeamColor();
        } return false;
    }

    public boolean isOpen(int row, int col) {
        if (offBoard(row, col)) {
            return false;
        } else return board.getPiece(new ChessPosition(row, col)) == null;
    }

    public boolean offBoard(int row, int col) {
        Set<Integer> validCoordinates = Set.of(1, 2, 3, 4, 5, 6, 7, 8);
        return !validCoordinates.contains(row) || !validCoordinates.contains(col);
    }
}