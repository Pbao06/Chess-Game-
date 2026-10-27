# Chess Game — Domain Model

This document explains how the classes in `src/main/java/com/chessgame/model/` relate to each
other and to the rest of the application. It's written for someone new to Spring Boot who already
knows Java and is familiar with ASP.NET.

Read this alongside the code in `src/main/java/com/chessgame/`.

---

## 1. Layers and how they map from what you already know

| Spring Boot concept        | Folder           | What it is in chess terms                     | ASP.NET analogue                |
| -------------------------- | ---------------- | --------------------------------------------- | ------------------------------- |
| **Domain model**           | `model/`         | The chess rules and state (Board, Piece, etc.)| Models / domain classes         |
| **Service (business layer)**| `service/`       | Game rules execution (turns, validation)      | Services / Business logic layer |
| **Controller (REST API)**  | `controller/`    | HTTP endpoints (`/api/games/...`)             | API Controller                  |
| **Exception handling**     | `exception/`     | Translates errors into HTTP responses         | Exception filter / middleware   |
| **Entry point**            | `*.ChessGameApplication` | Boots the Spring application           | `Program.cs` `Main()`           |

> In ASP.NET MVC an incoming request goes `Controller -> Service -> Model`. In Spring Boot the
> flow is identical here: `GameController -> GameService -> GameEngine -> Board/Piece`.

---

## 2. The domain model — relationships

```
 ┌────────────┐  owns        ┌──────────┐ 1..64 ┌─────────┐
 │   Board    │--------------│  Piece   │--------│ Piece   │
 │ (8x8 grid) │  has many     │[] [] []  │ has a  │[] [] [] │
 └─────┬──────┘                └────┬─────┘        └─────────┘
       │ holds the game state
       └─────────────── has a ┌────┴─────┐          ┌─────────────┐
                       │ GameEngine   │ uses ► │ MoveValidator │
                       └────┬────┬────┘          └──────┬──────┘
                            │    │ turn (PlayerColor)      │ validates against Board
                            │    └────────────────────────│
                            │ state (GameState)           │
                        ┌───▼───┐  manages many          │
                        │ Game  │◄──────── GameService    │
                        │Engine │  (Map<String,Engine>)   │
                        └───────┘                         │
                                 ▲                       │
            ┌────────────────────┴───────────────┐       │
            │ GameController (REST endpoints)    │       │
            │  POST /api/games  -> createGame()  │       │
            │  GET  /api/games/{id}               │       │
            │  POST /api/games/{id}/moves        │       │
            └────────────────▲────────────────────┘       │
                           │ uses                       │
                           └────────────────────────────┘

  Value objects / enums:
    Position  — (row, col), the coordinate of a square
    Move      — (from Position, to Position, promotion PieceType)
    PlayerColor — WHITE | BLACK
    PieceType   — PAWN | KNIGHT | BISHOP | ROOK | QUEEN | KING
    GameState   — ONGOING | CHECK | CHECKMATE | STALEMATE | DRAW
```

### 2.1 `Position` — where on the board
- A value type: `Position(row, col)`, both `0..7`.
- The board is indexed `row 0` (White's back rank area side) up to `row 7`.
- `Position` is used by `Move`, `Board.getPiece/setPiece/movePiece`.
- Think of it like an `(x, y)` coordinate struct in C#.

### 2.2 `PieceType` and `PlayerColor` — enums
- `PieceType`: the kind of piece. Each also has a `symbol(color)` for display.
- `PlayerColor`: `WHITE` or `BLACK`; has `opposite()` so you can flip the current player.

### 2.3 `Piece` — the thing that moves
- Holds a `PieceType` and a `PlayerColor`.
- It is a plain data class (like a C# record / POCO). It carries **no** move logic — the rules
  live in `MoveValidator`, so `Piece` stays reusable and dumb.

### 2.4 `Board` — the playing surface
- Contains `Piece[][] squares` — an 8x8 grid (64 squares).
- Responsibilities:
  - `Board()` builds the **standard starting position** (pawns on rows 1 & 6, pieces on 0 & 7).
  - `getPiece(Position)` / `setPiece(Position, Piece)` — read/write a square.
  - `movePiece(from, to)` — relocates a piece (overwrites the destination, captures if enemy).
  - `copy()` — returns a deep copy (useful if you ever implement AI / move search).
- Composition: **Board *has-many* Piece** (it owns them).

### 2.5 `Move` — a player action
- Value type: `Move(from, to)`, optional `promotion` (a `PieceType`, used when a pawn promotes).
- It is **not** responsible for whether the move is legal — that is `MoveValidator`'s job.

### 2.6 `MoveValidator` — the rule book
- Stateless helper. Entry point: `isValidMove(board, move, color)`.
- Checks, per piece type:
  - Pawn: 1-step forward, 2-step from start, diagonal capture (no en passant yet).
  - Knight: L-shape.
  - Bishop / Rook / Queen: diagonal / straight / combined, with blocked-path detection.
  - King: 1-square any direction (no castling yet).
- It never mutates the board — it only reads it.

### 2.7 `GameState` — match status
- Enum: `ONGOING, CHECK, CHECKMATE, STALEMATE, DRAW`.
- Currently always set to `ONGOING` after a move (stub). **Your task** is to compute the real
  state: detect check/checkmate/stalemate in `GameEngine.updateState()`.

### 2.8 `GameEngine` — one match, in memory
- Has one `Board`, whose `turn` it is (`PlayerColor`), and the `GameState`.
- `makeMove(Move)`:
  1. asks `MoveValidator` if the move is legal for the current turn,
  2. if not → throws `IllegalMoveException`,
  3. applies it via `Board.movePiece`, handles promotion,
  4. switches the turn (`turn.opposite()`),
  5. updates state.
- Think of this as your **chess match class** — equivalent to a service object that holds one game.

### 2.9 `GameService` — manages many games (Spring `@Service`)
- Keeps `Map<String, GameEngine> games` keyed by a UUID string id.
- `createGame()` → new `GameEngine`, returns its id.
- `getState(id)`, `getTurn(id)`, `makeMove(id, move)` → look up the engine (throws
  `GameNotFoundException` if absent) and delegate.
- In-memory only — your task is to persist it (DB / file) if you want games to survive a restart.

### 2.10 `GameController` — the HTTP layer (Spring `@RestController`)
- `@RequestMapping("/api/games")`.
- `POST /api/games` → `createGame()`, returns `{ "id": "<uuid>" }`.
- `GET /api/games/{id}` → `{ "state": "...", "turn": "..." }`.
- `POST /api/games/{id}/moves` with body `{ "fromRow", "fromCol", "toRow", "toCol" }` → applies the move.
- `MoveRequest` is a DTO (data transfer object) — like a C# request model. It converts to a domain
  `Move` before calling the service.

### 2.11 `exception/` — errors mapped to HTTP
- `GameException` (base, a `RuntimeException`).
- `IllegalMoveException` → HTTP `400 Bad Request` (bad move).
- `GameNotFoundException` → HTTP `404 Not Found`.
- `IllegalArgumentException` (e.g., `Position` out of bounds) → HTTP `400`.
- `GameExceptionHandler` is a `@RestControllerAdvice` — like an ASP.NET exception filter /
  `ProblemDetails` middleware.

---

## 3. Request lifecycle (a move in action)

1. Client sends `POST /api/games/{id}/moves` with `{ "fromRow":6, "fromCol":4, "toRow":4, "toCol":4 }`.
2. `GameController` turns that into a `Move` (two `Position`s) and calls `GameService.makeMove`.
3. `GameService` looks up the `GameEngine` by id (404 if missing).
4. `GameEngine.makeMove` calls `MoveValidator.isValidMove(board, move, turn)`.
5. If illegal → `GameValidator` throws `IllegalMoveException` → `GameExceptionHandler` returns `400`.
6. If legal → `Board.movePiece` updates the grid, turn flips, state updates.
7. `GameController` returns the new `{ "state", "turn" }`.

---

## 4. What to research / build next

- **Check / mate detection**: scan all enemy moves against the king's square; stalemate = no legal
  moves but not in check. Hook into `GameEngine.updateState()`.
- **Castling**: king/rook unmoved + no squares attacked. Needs move history (extend `GameEngine`).
- **En passant**: track the last double-pawn move.
- **Pawn promotion via API**: add `promotion` to `MoveRequest`.
- **Persistence**: replace the in-memory `Map` with Spring Data (JPA) + an H2/DB, mapping `Board`/
  `Move` to stored rows.
- **Optional UI**: drop HTML/JS into `src/main/resources/static/` or `templates/`.

---

## 5. Spring Boot conventions to get familiar with

- `@SpringBootApplication` (on `ChessGameApplication`) = `@Configuration + @ComponentScan + @EnableAutoConfiguration`. It's what makes `@Service`/`@RestController`/`@RestControllerAdvice` auto-detected by Spring — like `Startup.cs` auto-wiring controllers/services.
- `@RestController` = controller that returns JSON instead of a view (like an API controller returning `IActionResult`).
- `@Service` = a Spring-managed singleton bean (a C# singleton service registered in DI).
- Constructor injection (e.g. `GameController(GameService gameService)`) = dependency injection. Spring
  wires `GameService` in automatically.
- Records (`Position`, `Move`, `MoveRequest`) are Java value types — like C# `record`s.
