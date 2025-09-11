package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public String toString() {
        return String.format("%s%s", startPosition, endPosition);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChessMove other)) return false;
        if (!other.startPosition.equals(startPosition)) {
//            System.out.println("Not Equal, startPosition => " + this.toString() + " & " + other.toString());
            return false;
        }
        if (!other.endPosition.equals(endPosition)) {
//            System.out.println("Not Equal, endPosition => " + this.toString() + " & " + other.toString());
            return false;
        }
        if (other.promotionPiece != null && !other.promotionPiece.equals(promotionPiece)){
//            System.out.println("Not Equal, promotionPiece => " + this.toString() + " & " + other.toString());
            return false;
        }
//        System.out.println("Equal => " + this.toString() + " & " + other.toString());
        return true;
    }
}