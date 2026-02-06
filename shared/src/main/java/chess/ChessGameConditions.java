package chess;

import java.util.Collection;

public class ChessGameConditions {

    private final ChessGame.TeamColor teamColor;
    private final ChessBoard board;

    ChessGameConditions(ChessGame.TeamColor teamColor, ChessBoard board) {
        this.teamColor = teamColor;
        this.board = board;
    }

    public boolean isInCheck() {
        // would I need to check if a certain move would put the other piece in check?
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition piecePosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(piecePosition);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, piecePosition);
                    for (ChessMove move: moves) {
                        ChessPiece capturePiece = board.getPiece(move.getEndPosition());
                        if (capturePiece != null && capturePiece.getPieceType() == ChessPiece.PieceType.KING) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
