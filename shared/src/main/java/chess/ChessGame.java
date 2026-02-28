package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) { return null; }
        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();

//        implement validMoves functionality
        for (ChessMove move: allMoves) {
            ChessBoard testBoard = board.clone();

            ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
            if (move.getPromotionPiece() != null) {
                piece = new ChessPiece(teamColor, promotionPiece);
            }

            testBoard.addPiece(move.getStartPosition(), null);
            testBoard.addPiece(move.getEndPosition(), piece);
            if (!givenBoardIsInCheck(teamColor, testBoard)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        if (board.getPiece(startPosition) == null) {
            throw new InvalidMoveException("There is no piece at the starting position of this move.");
        }

        TeamColor moveTeam = board.getPiece(startPosition).getTeamColor();
        if (moveTeam != teamTurn) {
            throw new InvalidMoveException("Invalid move: it is not this team's turn.");
        }

        Collection<ChessMove> validMoves = validMoves(startPosition);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        ChessPiece piece;
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) {
            piece = new ChessPiece(moveTeam, promotionPiece);
        } else {
            piece = board.getPiece(startPosition);
        }

        board.addPiece(startPosition, null);
        board.addPiece(endPosition, piece);

        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else { teamTurn = TeamColor.WHITE; }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // would I need to check if a certain move would put the other piece in check?
        return givenBoardIsInCheck(teamColor, board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition piecePosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(piecePosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, piecePosition);
                    for (ChessMove move: moves) {
                        ChessBoard testBoard = board.clone();
                        testBoard.addPiece(move.getStartPosition(), null);
                        testBoard.addPiece(move.getEndPosition(), piece);
                        if (!givenBoardIsInCheck(teamColor, testBoard)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition piecePosition = new ChessPosition(i, j);
                    ChessPiece piece = board.getPiece(piecePosition);
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> validMoves = validMoves(piecePosition);
                        if (!validMoves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;

    }

    public boolean givenBoardIsInCheck(TeamColor teamColor, ChessBoard board) {
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

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
