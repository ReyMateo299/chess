package chess.moveGenerators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Set;

public class RookMoveGenerator extends MoveGenerator {

    public RookMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    public Set<ChessMove> generate() {
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        return generateMany(directions);
    }
}
