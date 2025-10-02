package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor teamTurn;
    private ChessBoard gameBoard;

    public ChessGame() {
        this.teamTurn = ChessGame.TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    public ChessGame(ChessGame.TeamColor Turn, ChessBoard Board) {
        this.teamTurn = Turn;
        this.gameBoard = Board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), gameBoard);
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
        this.teamTurn = team;
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
        if (gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        ChessPiece pieceToCheck = gameBoard.getPiece(startPosition);
        ChessGame.TeamColor pieceColor = gameBoard.getPiece(startPosition).getTeamColor();
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        moves.addAll(pieceToCheck.pieceMoves(gameBoard, startPosition));
        Iterator<ChessMove> moveItr = moves.iterator();
        while (moveItr.hasNext()) {
            ChessMove potentialMove = moveItr.next();
            ChessGame boardCheck = new ChessGame(pieceColor, gameBoard);
            boardCheck.makeMove(potentialMove);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
                                                                        //add proper logic
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece movingPiece = gameBoard.getPiece(start);
        gameBoard.addPiece(end, movingPiece);                           // add pawn logic
        //throw new InvalidMoveException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> enemyMoves = new ArrayList<ChessMove>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition square = new ChessPosition(i, j);
                if (gameBoard.getPiece(square) != null) {
                    if (gameBoard.getPiece(square).getTeamColor() != teamColor) { // if piece is an enemy
                        enemyMoves.addAll(gameBoard.getPiece(square).pieceMoves(gameBoard, square));
                        Iterator<ChessMove> moveItr = enemyMoves.iterator();
                        while (moveItr.hasNext()) {
                            ChessMove moveToCheck = moveItr.next();
                            ChessPosition spot = moveToCheck.getEndPosition();
                            if (gameBoard.getPiece(spot) != null) {
                                if (gameBoard.getPiece(spot).getPieceType() == ChessPiece.PieceType.KING && gameBoard.getPiece(spot).getTeamColor() == teamColor) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                enemyMoves.clear();
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.gameBoard;
    }
}
