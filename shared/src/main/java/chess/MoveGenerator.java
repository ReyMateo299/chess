package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates a list of valid moves for a given piece
 */
public class MoveGenerator {

    private ChessBoard board;
    private ChessPosition myPosition;

    public MoveGenerator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> generate() {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        return moves;
    }

    public boolean offBoard(ChessPosition position) {
        Set<Integer> validCoordinates = Set.of(1, 2, 3, 4, 5, 6, 7, 8);
        return (!validCoordinates.contains(position.getRow()) || !validCoordinates.contains(position.getColumn()));
    }

    public boolean isOpen(ChessPosition position) {
        if (offBoard(position)) { return false; }
        return (board.getPiece(position) == null);
    }

    public boolean isEnemy(ChessPosition position) {
        if (offBoard(position)) { return false; }
        return (board.getPiece(position).getTeamColor() != board.getPiece(myPosition).getTeamColor());
    }

    public boolean isAlly(ChessPosition position) {
        if (offBoard(position)) { return false; }
        return (board.getPiece(position).getTeamColor() == board.getPiece(myPosition).getTeamColor());
    }

}
