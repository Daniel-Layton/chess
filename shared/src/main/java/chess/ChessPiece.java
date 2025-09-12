package chess;

import java.util.Collection;
import java.util.ArrayList;

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

    public Collection<ChessMove> WhitePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int rowStart = myPosition.getRow();
        int colStart = myPosition.getColumn();
        if (rowStart < 8 && board.getPiece(new ChessPosition(rowStart+1, colStart)) == null) { // if square ahead is blank
            if (rowStart == 7) moves.addAll(Promotion(myPosition, new ChessPosition(rowStart+1, colStart)));
            else moves.add(new ChessMove(myPosition, new ChessPosition(rowStart+1, colStart), null));
            if (rowStart == 2 && board.getPiece(new ChessPosition(rowStart+2, colStart)) == null) moves.add(new ChessMove(myPosition, new ChessPosition(rowStart+2, colStart), null));
        }
        if (rowStart < 8 && colStart < 8 && board.getPiece(new ChessPosition(rowStart+1, colStart+1)) != null && board.getPiece(new ChessPosition(rowStart+1, colStart+1)).getTeamColor() != this.pieceColor) {
            if (rowStart == 7) moves.addAll(Promotion(myPosition, new ChessPosition(rowStart+1, colStart+1)));
            else moves.add(new ChessMove(myPosition, new ChessPosition(rowStart+1, colStart+1), null));
        }

        if (rowStart < 8 && colStart > 1 && board.getPiece(new ChessPosition(rowStart+1, colStart-1)) != null && board.getPiece(new ChessPosition(rowStart+1, colStart-1)).getTeamColor() != this.pieceColor) {
            if (rowStart == 7) moves.addAll(Promotion(myPosition, new ChessPosition(rowStart+1, colStart-1)));
            else moves.add(new ChessMove(myPosition, new ChessPosition(rowStart+1, colStart-1), null));
        }

        return moves;
    }

    public Collection<ChessMove> BlackPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int rowStart = myPosition.getRow();
        int colStart = myPosition.getColumn();
        if (rowStart > 1 && board.getPiece(new ChessPosition(rowStart-1, colStart)) == null) { // if square ahead is blank
            if (rowStart == 2) moves.addAll(Promotion(myPosition, new ChessPosition(rowStart-1, colStart)));
            else moves.add(new ChessMove(myPosition, new ChessPosition(rowStart-1, colStart), null));
            if (rowStart == 7 && board.getPiece(new ChessPosition(rowStart-2, colStart)) == null) moves.add(new ChessMove(myPosition, new ChessPosition(rowStart-2, colStart), null));
        }
        if (rowStart > 1 && colStart < 8 && board.getPiece(new ChessPosition(rowStart-1, colStart+1)) != null && board.getPiece(new ChessPosition(rowStart-1, colStart+1)).getTeamColor() != this.pieceColor) {
            if (rowStart == 2) moves.addAll(Promotion(myPosition, new ChessPosition(rowStart-1, colStart+1)));
            else moves.add(new ChessMove(myPosition, new ChessPosition(rowStart-1, colStart+1), null));
        }

        if (rowStart > 1 && colStart > 1 && board.getPiece(new ChessPosition(rowStart-1, colStart-1)) != null && board.getPiece(new ChessPosition(rowStart-1, colStart-1)).getTeamColor() != this.pieceColor) {
            if (rowStart == 2) moves.addAll(Promotion(myPosition, new ChessPosition(rowStart-1, colStart-1)));
            else moves.add(new ChessMove(myPosition, new ChessPosition(rowStart-1, colStart-1), null));
        }

        return moves;
    }

    private Collection<ChessMove> Promotion(ChessPosition myPos, ChessPosition newPos) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        moves.add(new ChessMove(myPos, newPos, PieceType.QUEEN));
        moves.add(new ChessMove(myPos, newPos, PieceType.BISHOP));
        moves.add(new ChessMove(myPos, newPos, PieceType.KNIGHT));
        moves.add(new ChessMove(myPos, newPos, PieceType.ROOK));
        return moves;
    }

    public Collection<ChessMove> RookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int rowStart = myPosition.getRow();
        int colStart = myPosition.getColumn();
        int rowDir = 1;
        int colDir = 0;
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

        rowDir = 0;
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
        colDir = 0;
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

        rowDir = 0;
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

    public Collection<ChessMove> KnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int rowStart = myPosition.getRow();
        int colStart = myPosition.getColumn();
        int[][] posMoves = new int[][]{{2, -1}, {2, 1}, {1, -2}, {1, 2}, {-1, -2}, {-1, 2}, {-2, -1}, {-2, 1}};
        for (int i = 0; i < posMoves.length; i++) {
            int rowNew = rowStart + posMoves[i][0];
            int colNew = colStart + posMoves[i][1];
            if (InBounds(rowNew, colNew)) {
                ChessPosition posNew = new ChessPosition(rowNew, colNew);
                ChessPiece pieceCheck = board.getPiece(posNew);
                if (pieceCheck != null) { // if piece in the way
                    if (pieceCheck.getTeamColor() == board.getPiece(myPosition).getTeamColor()) { // if piece in the way is on same team
                        continue;
                    } else { // if piece in the way is on the other team
                        moves.add(new ChessMove(myPosition, posNew, null));
                        continue;
                    }
                } else { // empty square
                    moves.add(new ChessMove(myPosition, posNew, null));
                }
            }
        }
        return moves;
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

        rowDir = 1;
        colDir = 0;
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

        rowDir = 0;
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
        colDir = 0;
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

        rowDir = 0;
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

    public Collection<ChessMove> KingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int rowStart = myPosition.getRow();
        int colStart = myPosition.getColumn();
        int[][] posMoves = new int[][]{{1, -1}, {1, 0}, {1, 1}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}};
        for (int i = 0; i < posMoves.length; i++) {
            int rowNew = rowStart + posMoves[i][0];
            int colNew = colStart + posMoves[i][1];
            if (InBounds(rowNew, colNew)) {
                ChessPosition posNew = new ChessPosition(rowNew, colNew);
                ChessPiece pieceCheck = board.getPiece(posNew);
                if (pieceCheck != null) { // if piece in the way
                    if (pieceCheck.getTeamColor() == board.getPiece(myPosition).getTeamColor()) { // if piece in the way is on same team
                        continue;
                    } else { // if piece in the way is on the other team
                        moves.add(new ChessMove(myPosition, posNew, null));
                        continue;
                    }
                } else { // empty square
                    moves.add(new ChessMove(myPosition, posNew, null));
                }
            }
        }
        return moves;
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
        PieceType selfType = this.getPieceType();
 //       System.out.println(selfType);
        if (selfType == PieceType.KING) return KingMoves(board, myPosition);
        else if (selfType == PieceType.QUEEN) return QueenMoves(board, myPosition);
        else if (selfType == PieceType.BISHOP) return BishopMoves(board, myPosition);
        else if (selfType == PieceType.KNIGHT) return KnightMoves(board, myPosition);
        else if (selfType == PieceType.ROOK) return RookMoves(board, myPosition);

        else if (selfType == PieceType.PAWN) {
            if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE)
                return WhitePawnMoves(board, myPosition);
            else return BlackPawnMoves(board, myPosition);
        }
        else throw new RuntimeException("Chess Piece Not In Position");
        }
    }