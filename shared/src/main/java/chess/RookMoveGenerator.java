package chess;

import java.util.HashSet;
import java.util.Set;

public class RookMoveGenerator extends MoveGenerator{

    private ChessBoard board;
    private ChessPosition myPosition;

    public RookMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    Set<ChessMove> generate() {
        return generateOrthogonals();
    }

}
