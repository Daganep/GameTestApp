# Используемые технологии

Все технологии уже подключены в проекте (см. `build.gradle.kts`) —
новые внешние зависимости не требуются.

## 1. Язык и сборка
| Технология | Версия | Назначение |
|---|---|---|
| Kotlin | 2.2.10 | Основной язык приложения |
| Android Gradle Plugin | 9.4.1 | Сборка APK |
| Gradle Kotlin DSL | — | Конфигурация сборки |
| compileSdk / minSdk / targetSdk | 37 / 24 / 37 | Целевые версии Android |

## 2. UI — Jetpack Compose
| Технология | Версия | Назначение |
|---|---|---|
| Compose BOM | 2026.02.01 | Синхронизация версий Compose |
| `compose-bom` + `ui`, `material3`, `ui-tooling-preview` | (BOM) | Отрисовка всех экранов игры |
| Compose `ui-test-junit4` | (BOM) | UI-тесты (already в зависимостях) |
| `lifecycle-runtime-ktx` | 2.11.1 | Жизненный цикл, запуск кодоутин на scope |

### Решение по архитектуре
- **Один activity** (`MainActivity`) + несколько Compose-экранов, переключение
  через sealed-состояние `Screen` (меню / игра / правила / конец игры).
  Библиотека Navigation не нужна — всего 4 состояния.
- **Состояние игры** — класс `GameEngine` (чистый Kotlin) +
  `remember`/`mutableStateOf` в Compose; состояние экранных слоёв
  хранится в `AppViewModel`-подобном `AppState` (simple holder).
  **ViewModel/Room/DataStore не добавляем** — игра не требует.
- **Игровой цикл** — `LaunchedEffect` + `kotlinx.coroutines.delay` (транзитивно
  доступна через lifecycle/Compose), тик 150…60 мс.
- **Отрисовка поля** — `Canvas` (compose.ui.graphics) для сетки/змейки/еды:
  быстрее и проще, чем 225 `Box`-композиций.
- **Управление** — `pointerInput` (свайпы) + Material3 `Button` (D-pad).
- **Память рекорда** — `androidx.core.contentpref` (SharedPreferences,
  `core-ktx` 1.17.0 уже в зависимостях). DataStore избыточен для одного int.
- **Тема** — Material 3 (`darkColorScheme`/`lightColorScheme`),
  портретная ориентация (зафиксирована в манифесте).

## 3. Тестирование
| Технология | Версия | Назначение |
|---|---|---|
| JUnit 4 | 4.13.2 | Юнит-тесты `GameEngine` (движение, коллизии, рост, спавн) |
| Compose `ui-test-junit4` | (BOM) | Интеграционные UI-тесты экранов |
| `espresso-core` | 3.7.0 | Базовый инструмент UI-тестов (уже в зависимостях) |

## 4. Структура пакетов
```
com.game.snake
├── MainActivity.kt          # единственный Activity, setContent
├── ui
│   ├── theme/               # Theme.kt, Color.kt (Material 3)
│   ├── AppScreen.kt         # корневой composable, переключение экранов
│   ├── MenuScreen.kt
│   ├── GameScreen.kt        # Canvas-поле, счёт, D-pad, свайпы
│   ├── HowToScreen.kt
│   └── GameOverScreen.kt
├── game                     # чистый Kotlin, без импортов android.*
│   ├── GameState.kt         # sealed: Running / Paused / GameOver
│   ├── Direction.kt         # enum + оператор canTurn
│   ├── Point.kt             # data class
│   ├── GameEngine.kt        # tick(), turn(), спавн еды, коллизии
│   └── Constants.kt         # размер поля, тики, очки
└── storage
    └── ScoreStorage.kt      # SharedPreferences-обёртка (рекорд)
```

## 5. Чего НЕ используем
- ❌ Unity / libGDX — игра простая, Compose достаточно.
- ❌ Room / DataStore — для одного числа (рекорд) достаточно SharedPreferences.
- ❌ Navigation Compose — всего 4 экрана, sealed-состояние проще.
- ❌ Hilt / DI-фреймворки — не нужен.
- ❌ Сетевые библиотеки — игра офлайн.
