package com.chessgame.service;

import com.chessgame.exception.GameNotFoundException;
import com.chessgame.model.GameState;
import com.chessgame.model.Move;
import com.chessgame.model.PlayerColor;
import com.chessgame.model.Position;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, GameEngine> games = new ConcurrentHashMap<>();

    public String createGame() {
        String id = UUID.randomUUID().toString();
        games.put(id, new GameEngine());
        return id;
    }

    public GameState getState(String id) {
        return getEngine(id).getState();
    }

    public PlayerColor getTurn(String id) {
        return getEngine(id).getTurn();
    }

    public void makeMove(String id, Position from, Position to) {
        getEngine(id).makeMove(from, to);
    }

    public void makeMove(String id, Move move) {
        getEngine(id).makeMove(move);
    }

    private GameEngine getEngine(String id) {
        GameEngine engine = games.get(id);
        if (engine == null) {
            throw new GameNotFoundException("Game not found: " + id);
        }
        return engine;
    }
}
