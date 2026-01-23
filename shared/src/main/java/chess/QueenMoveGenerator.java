package chess;

import java.util.HashSet;
import java.util.Set;

public class QueenMoveGenerator extends MoveGenerator{

    private ChessBoard board;
    private ChessPosition myPosition;

    public QueenMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    Set<ChessMove> generate() {
        Set<ChessMove> moves = generateOrthogonals();
        moves.addAll(generateDiagonals());
        return moves;
    }

}
