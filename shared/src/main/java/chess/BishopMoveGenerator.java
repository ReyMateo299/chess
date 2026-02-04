package chess;

import java.util.Set;

public class BishopMoveGenerator extends MoveGenerator{

    BishopMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    Set<ChessMove> generate() {
        int[][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        return generateMany(directions);
    }
}