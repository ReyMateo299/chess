package chess.movegenerators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Set;

public class KnightMoveGenerator extends MoveGenerator {

    public KnightMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    public Set<ChessMove> generate() {
        int[][] directions = {{2, -1}, {2, 1}, {1, -2}, {1, 2}, {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}};
        return generateSingle(directions);
    }
}
