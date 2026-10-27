package com.chessgame.model;

import java.util.Objects;

public final class Piece {

    private final PieceType type;
    private final PlayerColor color;

    public Piece(PieceType type, PlayerColor color) {
        this.type = Objects.requireNonNull(type, "type is required");
        this.color = Objects.requireNonNull(color, "color is required");
    }

    public PieceType type() {
        return type;
    }

    public PlayerColor color() {
        return color;
    }
}
