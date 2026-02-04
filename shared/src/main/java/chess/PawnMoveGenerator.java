package chess;

import java.util.HashSet;
import java.util.Set;

public class PawnMoveGenerator extends MoveGenerator{

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final int myRow;
    private final int myCol;
    private final ChessGame.TeamColor myTeam;

    PawnMoveGenerator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.board = board;
        this.myPosition = myPosition;
        this.myRow = myPosition.getRow();
        this.myCol = myPosition.getColumn();
        this.myTeam = board.getPiece(myPosition).getTeamColor();
    }

    Set<ChessMove> generate() {
        Set<ChessMove> moves = new HashSet<>();
        int forward;
        int[] horizontals = {-1, 1};

        if (myTeam == ChessGame.TeamColor.WHITE) {
            forward = 1;
        } else forward = -1;

        if (firstMove()) {
            if (isOpen(myRow + forward, myCol) && isOpen(myRow + forward*2, myCol)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myRow + forward*2, myCol), null));
            }
        }

        if (isOpen(myRow + forward, myCol)) {
            if (otherSide(myRow + forward)) {
                moves.addAll(generatePromotionMoves(myRow + forward, myCol));
            } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow + forward, myCol), null));
        }

        for (int horizontal: horizontals) {
            if (isEnemy(myRow + forward, myCol + horizontal)) {
                if (otherSide(myRow + forward)) {
                    moves.addAll(generatePromotionMoves(myRow + forward, myCol + horizontal));
                } else moves.add(new ChessMove(myPosition, new ChessPosition(myRow + forward, myCol + horizontal), null));
            }
        }

        return moves;
    }

    Set<ChessMove> generatePromotionMoves(int row, int col) {
        Set<ChessMove> moves = new HashSet<>();
        Set<ChessPiece.PieceType> promotionPieces = Set.of(
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN
        );

        for (ChessPiece.PieceType piece: promotionPieces) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), piece));
        }
        return moves;
    }

    public boolean otherSide(int row) {
        if (myTeam == ChessGame.TeamColor.WHITE) {
            return row == 8;
        } else return row == 1;
    }

    public boolean firstMove() {
        if (myTeam == ChessGame.TeamColor.WHITE) {
            return myRow == 2;
        } else return myRow == 7;
    }
}