# Technology Stack

All technologies are already in the project (see `build.gradle.kts`) —
no new external dependencies are needed.

## 1. Language & Build
| Technology | Version | Purpose |
|---|---|---|
| Kotlin | 2.2.10 | Main language of the app |
| Android Gradle Plugin | 9.4.1 | APK build |
| Gradle Kotlin DSL | — | Build configuration |
| compileSdk / minSdk / targetSdk | 37 / 24 / 37 | Target Android versions |

## 2. UI — Jetpack Compose
| Technology | Version | Purpose |
|---|---|---|
| Compose BOM | 2026.02.01 | Compose version alignment |
| `compose-bom` + `ui`, `material3`, `ui-tooling-preview` | (BOM) | Rendering all game screens |
| Compose `ui-test-junit4` | (BOM) | UI tests (already in dependencies) |
| `lifecycle-runtime-ktx` | 2.11.1 | Lifecycle, launching code on a scope |

### Architecture decisions
- **Single Activity** (`MainActivity`) + several Compose screens, switched
  via a sealed `Screen` state (menu / game / rules / game over).
  The Navigation library is not needed — only 4 states.
- **Game state** — a `GameEngine` class (pure Kotlin) +
  `remember`/`mutableStateOf` in Compose; screen-layer state
  is kept in an `AppViewModel`-like `AppState` (simple holder).
  **No ViewModel/Room/DataStore** — the game does not require them.
- **Game loop** — `LaunchedEffect` + `kotlinx.coroutines.delay`
  (available transitively via lifecycle/Compose), tick 150…60 ms.
- **Field rendering** — `Canvas` (compose.ui.graphics) for the grid/snake/food:
  faster and simpler than 225 `Box` compositions.
- **Controls** — `pointerInput` (swipes) + Material3 `Button` (D-pad).
- **Record persistence** — `androidx.core.contentpref` (SharedPreferences,
  `core-ktx` 1.17.0 already in dependencies). DataStore is overkill for one int.
- **Theme** — Material 3 (`darkColorScheme`/`lightColorScheme`),
  portrait orientation (fixed in the manifest).

## 3. Testing
| Technology | Version | Purpose |
|---|---|---|
| JUnit 4 | 4.13.2 | `GameEngine` unit tests (movement, collisions, growth, spawning) |
| Compose `ui-test-junit4` | (BOM) | Integration UI tests of screens |
| `espresso-core` | 3.7.0 | Base UI-testing tool (already in dependencies) |

## 4. Package Structure
```
com.game.snake
├── MainActivity.kt          # the only Activity, setContent
├── ui
│   ├── theme/               # Theme.kt, Color.kt (Material 3)
│   ├── AppScreen.kt         # root composable, screen switching
│   ├── MenuScreen.kt
│   ├── GameScreen.kt        # Canvas field, score, D-pad, swipes
│   ├── HowToScreen.kt
│   └── GameOverScreen.kt
├── game                     # pure Kotlin, no android.* imports
│   ├── GameState.kt         # sealed: Running / Paused / GameOver
│   ├── Direction.kt         # enum + canTurn operator
│   ├── Point.kt             # data class
│   ├── GameEngine.kt        # tick(), turn(), food spawn, collisions
│   └── Constants.kt         # field size, ticks, score
└── storage
    └── ScoreStorage.kt      # SharedPreferences wrapper (record)
```

## 5. What We Do NOT Use
- ❌ Unity / libGDX — the game is simple, Compose is enough.
- ❌ Room / DataStore — SharedPreferences is enough for a single number (record).
- ❌ Navigation Compose — only 4 screens, a sealed state is simpler.
- ❌ Hilt / DI frameworks — not needed.
- ❌ Networking libraries — the game is offline.
