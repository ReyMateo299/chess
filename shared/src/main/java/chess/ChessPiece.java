package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        // Set<ChessMove> moves = new HashSet<>();
        if (piece.getPieceType() == PieceType.KNIGHT) {
            chess.KnightMoveGenerator knGenerator = new KnightMoveGenerator(board, myPosition);
            return knGenerator.generate();
        } else if (piece.getPieceType() == PieceType.BISHOP) {
            chess.BishopMoveGenerator bGenerator = new BishopMoveGenerator(board, myPosition);
            return bGenerator.generate();
        } else if (piece.getPieceType() == PieceType.ROOK) {
            chess.RookMoveGenerator rGenerator = new RookMoveGenerator(board, myPosition);
            return rGenerator.generate();
        } else if (piece.getPieceType() == PieceType.QUEEN) {
            chess.QueenMoveGenerator qGenerator = new QueenMoveGenerator(board, myPosition);
            return qGenerator.generate();
        } else if (piece.getPieceType() == PieceType.KING) {
            chess.KingMoveGenerator kGenerator = new KingMoveGenerator(board, myPosition);
            return kGenerator.generate();
        }
        return List.of();
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    /*
     * Auto-generated code. Change?
     */
    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
