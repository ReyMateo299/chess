package chess;

/**
 * Generates a list of valid moves for a given piece
 */
public class MoveGenerator {

    private ChessBoard board;
    private ChessPosition myPosition;
    private ChessPiece piece;

    public MoveGenerator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        this.board = board;
        this.myPosition = myPosition;
        this.piece = piece;
    }

}
