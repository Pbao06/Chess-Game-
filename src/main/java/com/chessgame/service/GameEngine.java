package com.chessgame.service;

import com.chessgame.exception.IllegalMoveException;
import com.chessgame.model.Board;
import com.chessgame.model.GameState;
import com.chessgame.model.Move;
import com.chessgame.model.Piece;
import com.chessgame.model.PieceType;
import com.chessgame.model.PlayerColor;
import com.chessgame.model.Position;

public class GameEngine {

    private final Board board;
    private PlayerColor turn;
    private GameState state;

    public GameEngine() {
        this.board = new Board();
        this.turn = PlayerColor.WHITE;
        this.state = GameState.ONGOING;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerColor getTurn() {
        return turn;
    }

    public GameState getState() {
        return state;
    }

    public void makeMove(Position from, Position to) {
        makeMove(new Move(from, to));
    }

    public void makeMove(Move move) {
        MoveValidator validator = new MoveValidator();
        if (!validator.isValidMove(board, move, turn)) {
            throw new IllegalMoveException("Invalid move: " + move);
        }
        board.movePiece(move.from(), move.to());
        applyPromotion(move);
        turn = turn.opposite();
        updateState();
    }

    private void applyPromotion(Move move) {
        if (move.promotion() != null) {
            Piece moved = board.getPiece(move.to());
            if (moved != null && moved.type() == PieceType.PAWN) {
                board.setPiece(move.to(), new Piece(move.promotion(), turn));
            }
        }
    }

    private void updateState() {
        state = GameState.ONGOING;
    }
}
