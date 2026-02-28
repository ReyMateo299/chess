package chess.movegenerators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Set;

public class BishopMoveGenerator extends MoveGenerator {

    public BishopMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    public Set<ChessMove> generate() {
        int[][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        return generateMany(directions);
    }
}