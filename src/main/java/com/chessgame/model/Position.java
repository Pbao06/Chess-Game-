package com.chessgame.model;

public record Position(int row, int col) {

    public static final int SIZE = 8;

    public Position {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException(
                    "Position out of bounds: (" + row + ", " + col + ")");
        }
    }

    public boolean isValid() {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}
