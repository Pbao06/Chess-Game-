package com.chessgame.controller;

import com.chessgame.model.GameState;
import com.chessgame.model.Move;
import com.chessgame.model.PlayerColor;
import com.chessgame.model.Position;
import com.chessgame.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createGame() {
        String id = gameService.createGame();
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGameState(@PathVariable String id) {
        return ResponseEntity.ok(Map.of(
                "state", gameService.getState(id),
                "turn", gameService.getTurn(id)
        ));
    }

    @PostMapping("/{id}/moves")
    public ResponseEntity<Map<String, Object>> makeMove(@PathVariable String id, @RequestBody MoveRequest request) {
        Move move = new Move(
                new Position(request.fromRow(), request.fromCol()),
                new Position(request.toRow(), request.toCol())
        );
        gameService.makeMove(id, move);
        return ResponseEntity.ok(Map.of(
                "state", gameService.getState(id),
                "turn", gameService.getTurn(id)
        ));
    }

    public record MoveRequest(int fromRow, int fromCol, int toRow, int toCol) {
    }
}
