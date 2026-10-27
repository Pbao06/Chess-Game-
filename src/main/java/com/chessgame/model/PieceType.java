package com.chessgame.model;

public enum PieceType {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING;

    public char symbol(PlayerColor color) {
        char symbol = switch (this) {
            case PAWN -> 'p';
            case KNIGHT -> 'n';
            case BISHOP -> 'b';
            case ROOK -> 'r';
            case QUEEN -> 'q';
            case KING -> 'k';
        };
        return color == PlayerColor.WHITE ? Character.toUpperCase(symbol) : symbol;
    }
}
