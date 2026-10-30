# Snake App Requirements

## 1. Overview
Classic Snake arcade game for Android. The snake moves on a square grid, eats food, grows, and earns points. Death occurs on collision with a wall or its own body.

## 2. Functional Requirements

### 2.1 Game mechanics
- F-01. Board is a 15×15 square grid.
- F-02. Snake starts with 3 segments in the center, moving right.
- F-03. Snake moves 1 cell per tick; initial tick interval 150 ms.
- F-04. Direction changes only by 90° (180° reversal is invalid).
- F-05. Food spawns in a random free cell; only one food on the board.
- F-06. Eating food: +1 segment, score +10.
- F-07. Every 5 foods eaten: speed up (tick interval −10 ms, min 60 ms).
- F-08. Wall collision — game over.
- F-09. Self-collision — game over.
- F-10. Pause/resume at any time.

### 2.2 Screens
- F-11. Main menu: "Play", "How to play", high score display.
- F-12. Game screen: board, current score, speed, pause button.
- F-13. How-to-play screen: brief rules.
- F-14. Game over screen: final score, high score (with "New record!" mark), "Play again" and "Menu" buttons.

### 2.3 Controls
- F-15. Swipe gesture (up/down/left/right) anywhere on screen.
- F-16. Alternative control — on-screen D-pad at the bottom of the screen.

### 2.4 Data
- F-17. High score (max score) is stored locally and survives app restarts.
- F-18. Current score resets on new game.

## 3. Non-functional Requirements
- N-01. Target 60 FPS; game tick must not stutter the UI.
- N-02. minSdk 24, portrait orientation (locked).
- N-03. Offline app: no network requests or permissions.
- N-04. Game logic (movement, collisions, growth, food spawn) — pure Kotlin, no Android dependencies, fully covered by unit tests.
- N-05. Correct lifecycle handling: game auto-pauses when the app goes to background.
- N-06. Dark theme support (Material 3).
- N-07. Cell sizes computed from screen width (board always square, fits width with margins).

## 4. Constraints and Assumptions
- Single-player, no multiplayer.
- No sound effects (may be added later).
- No analytics, ads, or authentication.
- No third-party game engines (Unity/libGDX) — Compose only.

## 5. Acceptance Criteria
- [ ] Can start game from menu, play via swipe and D-pad, pause.
- [ ] Score grows, speed increases, high score persists across launches.
- [ ] Wall/self collision leads to game over screen with the score.
- [ ] `./gradlew test` — all core unit tests pass.
- [ ] App runs without errors on Android 7.0 (API 24) device.
