package chess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PawnMoveGenerator extends MoveGenerator{

    private ChessBoard board;
    private ChessPosition myPosition;
    private int myRow;
    private int myCol;
    private ChessGame.TeamColor myColor;

    public PawnMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.board = board;
        this.myPosition = myPosition;
        this.myRow = myPosition.getRow();
        this.myCol = myPosition.getColumn();
        this.myColor = board.getPiece(myPosition).getTeamColor();
    }

    Set<ChessMove> generate() {
        Set<ChessMove> moves = new HashSet<>();

        if (myColor == ChessGame.TeamColor.WHITE) {
            if (isOpen(myRow + 1, myCol)) {
                if (otherSide(myRow + 1)) {
                    moves.addAll(generatePromotionMoves(myRow + 1, myCol));
                } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow + 1, myCol), null));

                if (firstMove()) {
                    if (isOpen(myRow + 2, myCol)) {
                        if (otherSide(myRow + 2)) {
                            moves.addAll(generatePromotionMoves(myRow + 2, myCol));
                        } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow + 2, myCol), null));
                    }
                }
            }

            if (isEnemy(myRow + 1, myCol - 1)) {
                if (otherSide(myRow + 1)) {
                    moves.addAll(generatePromotionMoves(myRow + 1, myCol - 1));
                } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow + 1, myCol - 1), null));
            }

            if (isEnemy(myRow + 1, myCol + 1)) {
                if (otherSide(myRow + 1)) {
                    moves.addAll(generatePromotionMoves(myRow + 1, myCol));
                } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow + 1, myCol + 1), null));
            }
            return moves;
        } else

        if (isOpen(myRow - 1, myCol)) {
            if (otherSide(myRow - 1)) {
                moves.addAll(generatePromotionMoves(myRow - 1, myCol));
            } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow - 1, myCol), null));

            if (firstMove()) {
                if (isOpen(myRow - 2, myCol)) {
                    if (otherSide(myRow - 2)) {
                        moves.addAll(generatePromotionMoves(myRow - 2, myCol));
                    } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow - 2, myCol), null));
                }
            }
        }

        if (isEnemy(myRow - 1, myCol - 1)) {
            if (otherSide(myRow - 1)) {
                moves.addAll(generatePromotionMoves(myRow - 1, myCol - 1));
            } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow - 1, myCol - 1), null));
        }

        if (isEnemy(myRow - 1, myCol + 1)) {
            if (otherSide(myRow - 1)) {
                moves.addAll(generatePromotionMoves(myRow - 1, myCol));
            } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow - 1, myCol + 1), null));
        }

        return moves;
    }

    Set<ChessMove> generatePromotionMoves(int row, int col) {
        ChessPiece.PieceType[] promotions = {
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN
        };
        Set<ChessMove> moves = new HashSet<>();

        for (ChessPiece.PieceType promotion : promotions) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), promotion));
        }
        return moves;
    }

    boolean firstMove() {
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            return (myRow == 2);
        } else return (myRow == 7);
    }

    boolean otherSide(int row) {
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            return (row == 8);
        } else return (row == 1);
    }
}
