# Snake Game Development Plan

This document describes the sequence of work. Each stage ends with a
working, runnable application state (except Stage 0) and passing tests.

---

## Stage 0. Project Setup
**Goal:** project builds successfully, dependencies scaffold and folder structure in place.

### 0.1. Version Catalog and Dependencies
- [x] Add to `gradle/libs.versions.toml`: `compose-bom`, `material3`,
      `activity-compose`, `androidx.test.ext:junit` (for UI tests later),
      `junit5` libraries for core unit tests.
- [x] In `app/build.gradle.kts`: `testImplementation` — JUnit 5 (+ junit-vintage
      or migration to JUnit 4, depending on choice), `debugImplementation` — Compose UI Test.

### 0.2. Module Configuration
- [x] `android { namespace = "com.neway.gametestapp" }` (matches
      `applicationId`), `minSdk 24`, `compileSdk 37`, `targetSdk 37`.

### 0.3. Package Structure
- [x] Create directory tree and placeholder classes:
  ```
  com.neway.gametestapp
  ├── MainActivity.kt
  ├── ui
  │   ├── theme/          (Theme.kt, Color.kt, Type.kt)
  │   ├── menu/           (MainMenuScreen.kt)
  │   ├── game/           (GameScreen.kt, GameBoard.kt, DPad.kt)
  │   ├── howto/          (HowToScreen.kt)
  │   └── gameover/       (GameOverScreen.kt)
  ├── game
  │   ├── model/          (Direction, Point, GameState, GameConfig)
  │   ├── logic/          (SnakeGame, FoodSpawner, CollisionDetector)
  │   └── storage/        (ScoreStorage)
  └── util/               (SwipeDetector, GameTimer)
  ```

**Done when:** `./gradlew :app:assembleDebug` — BUILD SUCCESSFUL,
empty Activity launches on device/emulator.

---

## Stage 1. Game Core (Pure Kotlin)
**Goal:** all game logic without Android, 100% unit test coverage.

### 1.1. Data Models
- [x] `Direction` (enum: UP, DOWN, LEFT, RIGHT + `opposite()` method).
- [x] `Point(x, y)` — data class for cell coordinates.
- [x] `GameConfig` (size=15, startIntervalMs=150, minIntervalMs=60,
      speedUpEvery=5, intervalStepMs=10).

### 1.2. Game State Machine (`SnakeGame`)
- [x] Fields: `state: State` (PLAYING / PAUSED / GAME_OVER),
      `snake: List<Point>` (tail = head), `food: Point`, `score: Int`,
      `direction`, `pendingDirection`, `tickIntervalMs`.
- [x] `fun step()` — one tick: apply direction, check collisions
      (wall / body), eat food (+10, growth, new spawn, speed-up every 5).
- [x] `fun changeDirection(newDir)` — reject 180° turns and "no-direction" input.
- [x] `fun reset()` — initial state (3 segments in center, moving right).
- [x] `fun pause() / resume()`.

### 1.3. Food Generation
- [x] `FoodSpawner.randomFreeCell(snake, size, random)` — only free cells;
      if no cells remain — win (game over with special flag).

### 1.4. Unit Tests (`app/src/test/.../game/`)
- [x] Movement in each direction, no wrap (wall = death).
- [x] 180° turn is ignored.
- [x] Eating food: growth, +10, new spawn, food not on snake.
- [x] Speed-up every 5 food units, not below 60 ms.
- [x] Collision with body and wall → GAME_OVER.
- [x] `reset` fully resets state.

**Done when:** `./gradlew :app:testDebugUnitTest` — all tests
green, logical classes 100% branch coverage.

---

## Stage 2. High Score Storage
**Goal:** high score survives app restarts.

### 2.1. ScoreStorage Interface
- [x] Define interface
      `ScoreStorage { fun getBest(): Int; fun updateBest(score: Int) }`.
**Done when:** interface compiled, accessible from other
packages in the module.

### 2.2. PrefsScoreStorage Implementation
- [x] Implement `PrefsScoreStorage(context)` on `SharedPreferences`
      (file `snake_prefs`, key `best_score`).
- [x] Handle missing key (return 0 by default).
**Done when:** `getBest()` / `updateBest()` work correctly
on manual smoke check (or via instrumentation test).

### 2.3. Unit Tests
- [x] Test: initial value is 0.
- [x] Test: update on higher score.
- [x] Test: no change on lower score.
**Done when:** unit tests pass (with `Robolectric` or mocked
Context).

---

## Stage 3. Theme and Main Screen
**Goal:** visible app with a menu.

### 3.1. Theme
- [x] `ui/theme/`: Material 3, light + dark palettes
      (green accent theme for the "snake"), `Type.kt` default.

### 3.2. Navigation
- [x] `MainActivity`: `setContent { ... }`, navigation via
      `sealed class Screen { Menu, Game, HowTo, GameOver(val score: Int, val isNewRecord: Boolean) }`
      + `remember { mutableStateOf(Screen.Menu) }` (no Jetpack Navigation
      — only 4 screens).

### 3.3. Main Screen
- [x] `MainMenuScreen`: title "Snake", high score, "Play" button
      (Fill, primary), "How to Play" (Tonal), version.

### 3.4. "How to Play" Screen
- [x] `HowToScreen`: rules (swipe/arrows, food, collisions, speed),
      "Back" button.

**Done when:** app launches, screens switch,
dark theme works.

---

## Stage 4. Game Engine (SnakeGame.kt)
**Goal:** all game logic works without Android and is covered by tests.

### 4.1. Data Models
- [x] `core/model/Point.kt` — data class `Point(val x: Int, val y: Int)`.
- [x] `core/model/Direction.kt` — enum `UP/DOWN/LEFT/RIGHT`,
      `dx/dy` properties, `opposite()` method.
**Done when:** classes compiled, available from other packages.

### 4.2. SnakeGame Class (state and initialization)
- [x] `SnakeGame`: grid (e.g. 15×21), `snake: List<Point>`, `direction`,
      `food: Point`, `score`, `isGameOver`, `speedMs`.
- [x] Initial state: 3-segment snake in the center, direction RIGHT,
      food — random free cell.
**Done when:** `SnakeGame` object created, all fields
initialized, `isGameOver == false`.

### 4.3. step() Method
- [x] Move head in `direction`.
- [x] Tail growth when food is eaten (`score += 10`).
- [x] Collision check: wall and own body → `isGameOver = true`.
- [x] `randomFood()` generation off the snake (seeded `Random` for
      reproducibility).
- [x] Speed up: `speedMs −5 ms` per food (min. 90 ms).
**Done when:** one `step()` call correctly updates
snake position, score, speed and game state.

### 4.4. changeDirection() Method
- [x] Accept new direction from UI.
- [x] Ignore 180° reversal (e.g. UP → DOWN).
- [x] Ignore more than one turn per tick (e.g. UP → LEFT →
      DOWN in one tick — apply only the first one).
**Done when:** reversals handled per the rules,
snake cannot turn onto itself in one tick.

### 4.5. Unit Tests
- [x] Growth tests: eating food increases length by 1.
- [x] Wall tests: moving out of bounds → `isGameOver == true`.
- [x] Tail tests: collision with body → `isGameOver == true`.
- [x] Reversal tests: 180° is ignored.
- [x] Speed-up tests: `speedMs` decreases by 5 ms, not below 90.
- [x] Score tests: `score` increments correctly.
- [x] Food spawn tests: food never spawns on the snake
      (seeded `Random`, ≥ 100 iterations).
**Done when:** ≥ 15 tests, 100% coverage of key branches,
`./gradlew :app:testDebugUnitTest` green.

---

## Stage 5. Game Screen
**Goal:** playable "like a snake".

### 5.1. GameViewModel
- [x] `GameViewModel : AndroidViewModel` + `SnakeGame` from the `core` module;
      `MutableStateFlow<GameUiState>`.

### 5.2. Game loop (Ticker)
- [x] `Handler(Looper.getMainLooper())` + `postDelayed` with a dynamic
      interval from `speedMs`; start/pause/stop;
      `onCleared()` — guaranteed removal of the delayed message.

### 5.3. Rendering and input (`GameScreen`)
- [x] Canvas/Compose rendering: snake (rounded-rect segments, head),
      food (circle/"apple"), background grid; `BoxWithConstraints` for sizing.
- [x] Gestures: `detectVerticalSwipeGestures` + `detectHorizontalSwipeGestures`
      → `changeDirection`.
- [ ] `onKeyDown` emulation for keyboard input (optional).
- [x] "Pause" button, score and high score at the top.

### 5.4. Transition to "Game Over"
- [x] Transition to `Screen.GameOver(score, isNewRecord)` on `isGameOver`.
- [x] High score persistence via `ScoreStorage`.

**Done when:** a full game session on device/emulator;
pause works; high score is updated; no leaks (LeakCanary check is optional —
via a memory log over 10 sessions).

---

## Stage 6. "Game Over" screen, polish, release build
**Goal:** final screen, polish, and an installable build.

### 6.1. "Game Over" screen
- [ ] `GameOverScreen`: score, "New Record!" (conditional), "Play Again" /
      "Menu" buttons; appear animation.

### 6.2. UI and resources polish
- [ ] Sound effects (optional).
- [ ] Haptic feedback on death.
- [ ] Splash screen (Compose `SplashScreen`).
- [ ] App icon (adaptive icon).
- [ ] `strings.xml` — all text moved to resources.

### 6.3. Quality, build and release
- [ ] `lint` without errors, `testDebugUnitTest` green.
- [ ] `assembleRelease` + signing with a debug key for Play
      Internal verification (optional).
- [ ] README.md: how to build and run.

**Done when:** the app is ready to be installed on a real
device; a "what's next" list (additions: levels, bonuses, multiplayer).

---

## Order and dependencies

```
Stage 0 ──► Stage 1 ──► Stage 2 ──► Stage 4 ──► Stage 5 ──► Stage 6
             └────────► Stage 3 ────────┘
```
Stages 2 and 3 can be done in parallel; Stage 4 does not depend on the UI.

## Optimistic timeline (1 developer, ~4 h/day)

| Stage | Estimate |
|-------|----------|
| 0     | 0.5 d    |
| 1     | 0.5 d    |
| 2     | 0.5 d    |
| 3     | 1 d      |
| 4     | 1 d      |
| 5     | 1–1.5 d  |
| 6     | 0.5–1 d  |
| **Total** | **5–6 days** |

## Risks

| Risk | Mitigation |
|------|-----------|
| Memory leak in the ticker | `removeCallbacks` in `onCleared` + `onPause` |
| Double tick after resume | `isRunning` flag + restart the ticker only from `onResume` |
| Reversal onto itself within a single tick | buffer up to 2 directions in a queue |
| Random food "sticks" to the snake | `randomFood()` using a loop/set of free cells |

## Acceptance criteria (Definition of Done)

- [ ] The game is fully playable on Android 7.0+ (API 24+).
- [ ] High score is saved across launches.
- [ ] ≥ 15 unit tests for the engine, all green.
- [ ] Linter without errors; warnings — no more than 10.
- [ ] Dark theme, swipes, and pause work.
- [ ] README with build instructions.

