package chess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PawnMoveGenerator extends MoveGenerator{

    private ChessBoard board;
    private ChessPosition myPosition;
    private int myRow;
    private int myCol;

    public PawnMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.board = board;
        this.myPosition = myPosition;
        this.myRow = myPosition.getRow();
        this.myCol = myPosition.getColumn();
    }

    Set<ChessMove> generate() {
        Set<ChessMove> moves = new HashSet<>();

        if (isOpen(myRow + 1, myCol)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myRow + 1, myCol), null));
            if (firstMove()) {
                if (isOpen(myRow + 2, myCol)) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myRow + 2, myCol), null));
                }
            }
        }

        if (isEnemy(myRow + 1, myCol - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myRow + 1, myCol - 1), null));
        }

        if (isEnemy(myRow + 1, myCol + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myRow + 1, myCol + 1), null));
        }

        return moves;
    }

    boolean firstMove() {
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            return (myRow == 2);
        } else return (myRow == 7);
    }

    boolean otherSide() {
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            return (myRow == 8);
        } else return (myRow == 1);
    }
}
