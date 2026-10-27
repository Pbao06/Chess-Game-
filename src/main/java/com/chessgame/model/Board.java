package com.chessgame.model;

public class Board {

    public static final int SIZE = Position.SIZE;

    private final Piece[][] squares = new Piece[SIZE][SIZE];

    public Board() {
        setupStandardGame();
    }

    public Board(Board other) {
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(other.squares[r], 0, squares[r], 0, SIZE);
        }
    }

    private void setupStandardGame() {
        placePawns(PlayerColor.WHITE, 1);
        placePawns(PlayerColor.BLACK, 6);
        placeBackRank(PlayerColor.WHITE, 0);
        placeBackRank(PlayerColor.BLACK, 7);
    }

    private void placePawns(PlayerColor color, int row) {
        for (int c = 0; c < SIZE; c++) {
            squares[row][c] = new Piece(PieceType.PAWN, color);
        }
    }

    private void placeBackRank(PlayerColor color, int row) {
        PieceType[] rank = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };
        for (int c = 0; c < SIZE; c++) {
            squares[row][c] = new Piece(rank[c], color);
        }
    }

    public Piece getPiece(Position position) {
        return squares[position.row()][position.col()];
    }

    public Piece getPiece(int row, int col) {
        return squares[row][col];
    }

    public void setPiece(Position position, Piece piece) {
        squares[position.row()][position.col()] = piece;
    }

    public void movePiece(Position from, Position to) {
        squares[to.row()][to.col()] = squares[from.row()][from.col()];
        squares[from.row()][from.col()] = null;
    }

    public Board copy() {
        return new Board(this);
    }
}
