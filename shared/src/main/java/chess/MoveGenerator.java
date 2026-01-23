package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates a list of valid moves for a given piece
 */
public class MoveGenerator {

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final int myRow;
    private final int myCol;

    public MoveGenerator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.myRow = myPosition.getRow();
        this.myCol = myPosition.getColumn();
    }

    /*
    public Collection<ChessMove> generate() {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        return moves;
    }

     */

    public boolean offBoard(int row, int col) {
        Set<Integer> validCoordinates = Set.of(1, 2, 3, 4, 5, 6, 7, 8);
        return (!validCoordinates.contains(row) || !validCoordinates.contains(col));
    }

    public boolean isOpen(int row, int col) {
        if (offBoard(row, col)) { return false; }
        return (board.getPiece(new ChessPosition(row, col)) == null);
    }

    public boolean isEnemy(int row, int col) {
        if (offBoard(row, col)) { return false; }
        return (board.getPiece(new ChessPosition(row, col)).getTeamColor() != board.getPiece(myPosition).getTeamColor());
    }

    public boolean isAlly(int row, int col) {
        if (offBoard(row, col)) { return false; }
        return (board.getPiece(new ChessPosition(row, col)).getTeamColor() == board.getPiece(myPosition).getTeamColor());
    }

    public void addMoveIfEnemy(int row, int col, Set<ChessMove> moves) {
        if (isEnemy(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }
    }

    public Set<ChessMove> generateOrthogonals() {
        Set<ChessMove> moves = new HashSet<ChessMove>();

        // Right
        int row = myRow;
        int col = myCol + 1;
        while (isOpen(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            col++;
        }
        addMoveIfEnemy(row, col, moves);

        // Left
        col = myCol - 1;
        while (isOpen(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            col--;
        }
        addMoveIfEnemy(row, col, moves);

        // Up
        row = myRow + 1;
        col = myCol;
        while (isOpen(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            row++;
        }
        addMoveIfEnemy(row, col, moves);

        // Down
        row = myRow - 1;
        while (isOpen(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            row--;
        }
        addMoveIfEnemy(row, col, moves);

        return moves;
    }

    public Set<ChessMove> generateDiagonals() {
        Set<ChessMove> moves = new HashSet<ChessMove>();

        // Up Right
        int row = myRow + 1;
        int col = myCol + 1;
        while (isOpen(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            row++;
            col++;
        }
        addMoveIfEnemy(row, col, moves);

        // Down Right
        row = myRow - 1;
        col = myCol + 1;
        while (isOpen(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            row--;
            col++;
        }
        addMoveIfEnemy(row, col, moves);

        // Down Left
        row = myRow - 1;
        col = myCol - 1;
        while (isOpen(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            row--;
            col--;
        }
        addMoveIfEnemy(row, col, moves);

        // Up Left
        row = myRow + 1;
        col = myCol - 1;
        while (isOpen(row, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            row++;
            col--;
        }
        addMoveIfEnemy(row, col, moves);

        return moves;

    }

}
