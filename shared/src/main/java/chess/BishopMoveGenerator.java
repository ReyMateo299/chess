package chess;

import java.util.HashSet;
import java.util.Set;

public class BishopMoveGenerator extends MoveGenerator{

    private ChessBoard board;
    private ChessPosition myPosition;

    public BishopMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    Set<ChessMove> generate() {
        return generateDiagonals();
    }

}
