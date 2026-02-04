package chess;

import java.util.Set;

public class KnightMoveGenerator extends MoveGenerator{

    KnightMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    Set<ChessMove> generate() {
        int[][] directions = {{2, -1}, {2, 1}, {1, -2}, {1, 2}, {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}};
        return generateSingle(directions);
    }
}
