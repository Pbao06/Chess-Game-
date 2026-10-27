package com.chessgame.service;

import com.chessgame.model.Board;
import com.chessgame.model.Move;
import com.chessgame.model.Piece;
import com.chessgame.model.PieceType;
import com.chessgame.model.PlayerColor;
import com.chessgame.model.Position;

public class MoveValidator {

    public boolean isValidMove(Board board, Move move, PlayerColor color) {
        Piece piece = board.getPiece(move.from());
        if (piece == null || piece.color() != color) {
            return false;
        }

        Piece target = board.getPiece(move.to());
        if (target != null && target.color() == color) {
            return false;
        }

        int dRow = move.to().row() - move.from().row();
        int dCol = move.to().col() - move.from().col();

        return switch (piece.type()) {
            case PAWN -> validPawnMove(board, move, dRow, dCol, color);
            case KNIGHT -> validKnightMove(dRow, dCol);
            case BISHOP -> validBishopMove(dRow, dCol) && pathIsClear(board, move.from(), move.to());
            case ROOK -> validRookMove(dRow, dCol) && pathIsClear(board, move.from(), move.to());
            case QUEEN -> validQueenMove(dRow, dCol) && pathIsClear(board, move.from(), move.to());
            case KING -> validKingMove(dRow, dCol);
        };
    }

    private boolean validPawnMove(Board board, Move move, int dRow, int dCol, PlayerColor color) {
        int direction = color == PlayerColor.WHITE ? 1 : -1;
        int startRow = color == PlayerColor.WHITE ? 1 : 6;
        Piece target = board.getPiece(move.to());

        if (dCol == 0) {
            if (dRow == direction && target == null) {
                return true;
            }
            if (move.from().row() == startRow && dRow == 2 * direction && target == null
                    && board.getPiece(new Position(move.from().row() + direction, move.from().col())) == null) {
                return true;
            }
            return false;
        }

        if (Math.abs(dCol) == 1 && dRow == direction) {
            return target != null && target.color() != color;
        }
        return false;
    }

    private boolean validKnightMove(int dRow, int dCol) {
        return (Math.abs(dRow) == 2 && Math.abs(dCol) == 1)
                || (Math.abs(dRow) == 1 && Math.abs(dCol) == 2);
    }

    private boolean validBishopMove(int dRow, int dCol) {
        return Math.abs(dRow) == Math.abs(dCol) && dRow != 0;
    }

    private boolean validRookMove(int dRow, int dCol) {
        return (dRow == 0 && dCol != 0) || (dCol == 0 && dRow != 0);
    }

    private boolean validQueenMove(int dRow, int dCol) {
        return validRookMove(dRow, dCol) || validBishopMove(dRow, dCol);
    }

    private boolean validKingMove(int dRow, int dCol) {
        return Math.abs(dRow) <= 1 && Math.abs(dCol) <= 1 && (dRow != 0 || dCol != 0);
    }

    private boolean pathIsClear(Board board, Position from, Position to) {
        int dRow = Integer.signum(to.row() - from.row());
        int dCol = Integer.signum(to.col() - from.col());
        int r = from.row() + dRow;
        int c = from.col() + dCol;
        while (r != to.row() || c != to.col()) {
            if (board.getPiece(new Position(r, c)) != null) {
                return false;
            }
            r += dRow;
            c += dCol;
        }
        return true;
    }
}
