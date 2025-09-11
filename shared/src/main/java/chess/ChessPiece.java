package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
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

    private boolean InBounds(int row, int col) {
        if (row > 8 || row < 1) {
            return false;
        }
        else if (col > 8 || col < 1) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //Collection<ChessMove> moves = new ArrayList<>();
        //Collection<ChessMove> moves = BishopMoves(board, myPosition);
        //Collection<ChessMove> moves = List.of();
        return BishopMoves(board, myPosition);
    }

    public Collection<ChessMove> WhitePawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessMove> BlackPawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessMove> RookMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessMove> KnightMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int rowStart = myPosition.getRow();
        int colStart = myPosition.getColumn();
        int rowDir = 1;
        int colDir = 1;
        int distance = 0;
        int rowNew = 0;
        int colNew = 0;
        boolean lineCheck = true;

        while (lineCheck) {
            distance++;
            rowNew = rowStart + (rowDir * distance);
            colNew = colStart + (colDir * distance);
            if (InBounds(rowNew, colNew)) {
                ChessPosition posNew = new ChessPosition(rowNew, colNew);
                ChessPiece pieceCheck = board.getPiece(posNew);
                if (pieceCheck != null) { // if piece in the way
                    if (pieceCheck.getTeamColor() == board.getPiece(myPosition).getTeamColor()) { // if piece in the way is on same team
                        lineCheck = false;
                    }
                    else { // if piece in the way is on the other team
                        moves.add(new ChessMove(myPosition, posNew, null));
                        lineCheck = false;
                    }
                }
                else { // empty square
                    moves.add(new ChessMove(myPosition, posNew, null));
                }
            }
            else { // out of bounds
                lineCheck = false;
            }
        }

        rowDir = -1;
        colDir = 1;
        distance = 0;
        lineCheck = true;

        while (lineCheck) {
            distance++;
            rowNew = rowStart + (rowDir * distance);
            colNew = colStart + (colDir * distance);
            if (InBounds(rowNew, colNew)) {
                ChessPosition posNew = new ChessPosition(rowNew, colNew);
                ChessPiece pieceCheck = board.getPiece(posNew);
                if (pieceCheck != null) { // if piece in the way
                    if (pieceCheck.getTeamColor() == board.getPiece(myPosition).getTeamColor()) { // if piece in the way is on same team
                        lineCheck = false;
                    }
                    else { // if piece in the way is on the other team
                        moves.add(new ChessMove(myPosition, posNew, null));
                        lineCheck = false;
                    }
                }
                else { // empty square
                    moves.add(new ChessMove(myPosition, posNew, null));
                }
            }
            else { // out of bounds
                lineCheck = false;
            }
        }

        rowDir = -1;
        colDir = -1;
        distance = 0;
        lineCheck = true;

        while (lineCheck) {
            distance++;
            rowNew = rowStart + (rowDir * distance);
            colNew = colStart + (colDir * distance);
            if (InBounds(rowNew, colNew)) {
                ChessPosition posNew = new ChessPosition(rowNew, colNew);
                ChessPiece pieceCheck = board.getPiece(posNew);
                if (pieceCheck != null) { // if piece in the way
                    if (pieceCheck.getTeamColor() == board.getPiece(myPosition).getTeamColor()) { // if piece in the way is on same team
                        lineCheck = false;
                    }
                    else { // if piece in the way is on the other team
                        moves.add(new ChessMove(myPosition, posNew, null));
                        lineCheck = false;
                    }
                }
                else { // empty square
                    moves.add(new ChessMove(myPosition, posNew, null));
                }
            }
            else { // out of bounds
                lineCheck = false;
            }
        }

        rowDir = 1;
        colDir = -1;
        distance = 0;
        lineCheck = true;

        while (lineCheck) {
            distance++;
            rowNew = rowStart + (rowDir * distance);
            colNew = colStart + (colDir * distance);
            if (InBounds(rowNew, colNew)) {
                ChessPosition posNew = new ChessPosition(rowNew, colNew);
                ChessPiece pieceCheck = board.getPiece(posNew);
                if (pieceCheck != null) { // if piece in the way
                    if (pieceCheck.getTeamColor() == board.getPiece(myPosition).getTeamColor()) { // if piece in the way is on same team
                        lineCheck = false;
                    }
                    else { // if piece in the way is on the other team
                        moves.add(new ChessMove(myPosition, posNew, null));
                        lineCheck = false;
                    }
                }
                else { // empty square
                    moves.add(new ChessMove(myPosition, posNew, null));
                }
            }
            else { // out of bounds
                lineCheck = false;
            }
        }

        return moves;
    }

    public Collection<ChessMove> QueenMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessMove> KingMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

}