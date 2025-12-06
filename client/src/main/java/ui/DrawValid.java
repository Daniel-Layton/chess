package ui;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DrawValid {
    ChessGame game;
    Integer row;
    Integer col;
    ArrayList<String> letterList;
    ArrayList<String> numList;
    HashSet<ChessPosition> validSquares;

    String LIGHT_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_DARK_GREEN; // Light squares
    String DARK_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_BLACK; // Dark squares
    String BORDER_COLOR = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    String TEXT_COLOR_GREY = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
    String TEXT_COLOR_LIGHT = EscapeSequences.SET_TEXT_COLOR_GREEN;
    String TEXT_COLOR_DARK = EscapeSequences.SET_TEXT_COLOR_BLUE;
    String HIGHLIGHT_LIGHT = EscapeSequences.SET_BG_COLOR_RED;
    String HIGHLIGHT_DARK = EscapeSequences.SET_BG_COLOR_MAGENTA;

    public DrawValid(ChessGame state, ChessPosition start) {
        this.game = state;
        this.validSquares = new HashSet<>();
        for (ChessMove chessMove : state.validMoves(start)) {
            validSquares.add(chessMove.getEndPosition());
        }
    }

    public String draw(boolean flip) {
        if (!flip) {
            row = 8;
            col = 1;
            letterList = new ArrayList<>(List.of(" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "));
            numList = new ArrayList<>(List.of(" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "));
        }
        else {
            row = 1;
            col = 8;
            letterList = new ArrayList<>(List.of(" h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "));
            numList = new ArrayList<>(List.of(" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "));
        }

        System.out.print(BORDER_COLOR);
        System.out.print(TEXT_COLOR_GREY);
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(letterList.get(i));
        }
        System.out.print("   ");

        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.println();

        boolean toggle = true;
        for (int i = 0; i < 8; i++) {

            System.out.print(BORDER_COLOR);
            System.out.print(TEXT_COLOR_GREY);
            System.out.print(numList.get(i));

            for (int j = 0; j < 8; j++) {
                boolean validSquare = false;
                ChessPosition squareToCheck = new ChessPosition(row, col);
                if (validSquares.contains(squareToCheck)) validSquare = true;
                if (toggle) {System.out.print(LIGHT_SQUARE_COLOR);}
                else {System.out.print(DARK_SQUARE_COLOR);}
                if (toggle && validSquare) {System.out.print(HIGHLIGHT_LIGHT);}
                else if (validSquare) {System.out.print(HIGHLIGHT_DARK);}
                toggle = !toggle;
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
                printPiece(piece);
                incSquare(flip);
            }
            toggle = !toggle;

            System.out.print(BORDER_COLOR);
            System.out.print(TEXT_COLOR_GREY);
            System.out.print(numList.get(i));

            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println();

        }

        System.out.print(BORDER_COLOR);
        System.out.print(TEXT_COLOR_GREY);
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(letterList.get(i));
        }
        System.out.print("   ");

        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        return " ";
    }

    private void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print("   ");
            return;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) System.out.print(TEXT_COLOR_LIGHT);
        else System.out.print(TEXT_COLOR_DARK);

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) System.out.print(" P ");
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) System.out.print(" R ");
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) System.out.print(" N ");
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) System.out.print(" B ");
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) System.out.print(" Q ");
        if (piece.getPieceType() == ChessPiece.PieceType.KING) System.out.print(" K ");
    }

    private void incSquare(Boolean flip) {
        if (!flip) {
            col++;
            if (col > 8) {
                col = 1;
                row--;
            }
        }
        else {
            col--;
            if (col < 1) {
                col = 8;
                row++;
            }
        }
    }
}
