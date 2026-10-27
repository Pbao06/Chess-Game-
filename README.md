# Chess Game

A Spring Boot project that provides a REST API for playing chess. This repository is set up as a
scaffold: the build, project structure, and core domain plumbing are in place so you can code the
rest your own way.

## Requirements

- Java 21 (JDK 21+). The project targets `java.version=21`; any newer JDK (e.g. 25) works.
- Maven is **not** required. Use the included wrapper: `./mvnw` (Unix) or `mvnw.cmd` (Windows).

## Build

```bash
./mvnw compile          # compile sources
./mvnw test             # run tests
./mvnw spring-boot:run  # run the application
```

The application starts on port `8080` (default) with the Spring Web MVC stack.

## API

Endpoints live under `/api/games`.

| Method   | Path              | Description                                          |
| -------- | ----------------- | ---------------------------------------------------- |
| `POST`   | `/api/games`      | Create a new game. Returns `{ "id": "<uuid>" }`.     |
| `GET`    | `/api/games/{id}` | Game state. Returns `{ "state", "turn" }`.           |
| `POST`   | `/api/games/{id}/moves` | Submit a move: `{ "fromRow", "fromCol", "toRow", "toCol" }`. |

## Project structure

```
src/main/java/com/chessgame/
├── ChessGameApplication.java      # Spring Boot entry point
├── controller/
│   └── GameController.java        # REST API (create game, get state, submit move)
├── model/                         # Domain model
│   ├── Board.java
│   ├── GameState.java
│   ├── Move.java
│   ├── Piece.java
│   ├── PieceType.java
│   ├── PlayerColor.java
│   └── Position.java
├── service/
│   ├── GameEngine.java            # Runs a single game (turn handling, move application)
│   ├── GameService.java           # Manages games by id (Spring @Service, in-memory)
│   └── MoveValidator.java         # Move legality rules
└── exception/
    ├── GameException.java         # Base exception
    ├── IllegalMoveException.java
    ├── GameNotFoundException.java
    └── GameExceptionHandler.java  # Maps exceptions to HTTP responses
```

## What's here vs. what's yours to build

The scaffold implements the plumbing and the basic pieces:
- Board setup (standard chess starting position), `Piece`/`Position`/`Move` value types
- Move validation for standard movement patterns (pawn, knight, bishop, rook, queen, king)
- REST endpoints to create a game, read its state, and submit moves
- Centralised exception handling

Pieces left for you to implement:
- Full check / checkmate / stalemate detection (the engine currently sets `ONGOING`)
- Castling, en passant, and pawn promotion via the API
- Game persistence (the service is in-memory for now)
- A frontend (there's a `resources/templates` folder ready for a UI)

## Build info

- Group id: `com.chessgame`
- Artifact id: `chess-game`
- Spring Boot: `4.1.2`
- Java: `21`
