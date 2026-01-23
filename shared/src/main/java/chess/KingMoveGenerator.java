package chess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KingMoveGenerator extends MoveGenerator{

    private ChessBoard board;
    private ChessPosition myPosition;
    private int myRow;
    private int myCol;

    public KingMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.board = board;
        this.myPosition = myPosition;
        this.myRow = myPosition.getRow();
        this.myCol = myPosition.getColumn();
    }

    Set<ChessMove> generate() {
        Set<ChessMove> moves = new HashSet<>();
        int[][] directions = {{1, -1}, {1, 0}, {1, 1}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}};

        for (int i = 0; i < 8; i++){
            if (isOpen(myRow + directions[i][0], myCol + directions[i][1])
                    || isEnemy(myRow + directions[i][0], myCol + directions[i][1])) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myRow + directions[i][0], myCol + directions[i][1]), null));
            }
        }

        return moves;
    }
}
