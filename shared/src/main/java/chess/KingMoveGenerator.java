package chess;

import java.util.Set;

public class KingMoveGenerator extends MoveGenerator{

    KingMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    Set<ChessMove> generate() {
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        return generateSingle(directions);
    }
}