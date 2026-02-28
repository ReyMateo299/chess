package chess.movegenerators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Set;

public class KingMoveGenerator extends MoveGenerator {

    public KingMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    public Set<ChessMove> generate() {
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        return generateSingle(directions);
    }
}