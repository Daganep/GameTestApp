# Этапы разработки игры «Змейка»

Документ описывает последовательность работ. Каждый этап завершается
рабочим, запускаемым состоянием приложения (кроме 0-го) и пройденными
тестами.

---

## Этап 0. Подготовка проекта
**Цель:** проект собирается, есть каркас зависимостей и структура папок.

- [ ] В `gradle/libs.versions.toml` добавить: `compose-bom`, `material3`,
      `activity-compose`, `androidx.test.ext:junit` (для UI-тестов позже),
      `junit5`-библиотеки для юнит-тестов ядра.
- [ ] В `app/build.gradle.kts`: `testImplementation` — JUnit 5 (+junit-vintage
      или миграция на JUnit 4, в зависимости от выбора), `debugImplementation` — Compose UI Test.
- [ ] `android { namespace = "com.example.gametestapp" }` (совпадает с
      `applicationId`), `minSdk 24`, `compileSdk 36`, `targetSdk 36`.
- [ ] Структура пакетов:
  ```
  com.example.gametestapp
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
**Критерий завершения:** `./gradlew :app:assembleDebug` — BUILD SUCCESSFUL,
пустой Activity запускается на устройстве/эмуляторе.

---

## Этап 1. Игровое ядро (чистый Kotlin)
**Цель:** вся логика игры без Android, 100% покрыта юнит-тестами.

- [ ] `Direction` (enum: UP, DOWN, LEFT, RIGHT + метод `opposite()`),
      `Point(x, y)`, `GameConfig` (size=15, startIntervalMs=150, minIntervalMs=60,
      speedUpEvery=5, intervalStepMs=10).
- [ ] Класс `SnakeGame` (state machine):
  - `state: State` (PLAYING / PAUSED / GAME_OVER),
  - `snake: List<Point>` (хвост = голова), `food: Point`, `score: Int`,
  - `direction`, `pendingDirection`, `tickIntervalMs`,
  - `fun step()` — один тик: применить направление, проверить коллизии
    (стена / тело), съесть еду (+10, рост, новый спавн, ускорение каждые 5),
  - `fun changeDirection(newDir)` — отклонить разворот на 180° и направление
    «в никуда»;
  - `fun reset()` — начальное состояние (3 сегмента в центре, движение вправо);
  - `fun pause() / resume()`.
- [ ] `FoodSpawner.randomFreeCell(snake, size, random)` — только свободные
      клетки; если клеток не осталось — победа (игра окончена со special flag).
- [ ] Юнит-тесты (`app/src/test/.../game/`):
  - движение по каждому направлению, wrap НЕ происходит (стена = смерть);
  - разворот на 180° игнорируется;
  - съедание еды: рост, +10, новый спавн, еда не на змейке;
  - ускорение каждые 5 единиц еды, не ниже 60 мс;
  - коллизия с телом и стеной → GAME_OVER;
  - reset полностью сбрасывает состояние.
**Критерий завершения:** `./gradlew :app:testDebugUnitTest` — все тесты
зелёные, покрытие логических классов — 100% ветвлений.

---

## Этап 2. Хранение рекорда
**Цель:** рекорд переживает перезапуск приложения.

- [ ] Интерфейс `ScoreStorage { fun getBest(): Int; fun updateBest(score: Int) }`.
- [ ] Реализация `PrefsScoreStorage(context)` на `SharedPreferences`
      (файл `snake_prefs`, ключ `best_score`).
- [ ] Тесты: обновление при большем счёте, отсутствие изменения при меньшем,
      начальное значение 0.
**Критерий завершения:** юнит-тесты проходят (с `Robolectric` или моканием
Context).

---

## Этап 3. Тема и главный экран
**Цель:** видимое приложение с меню.

- [ ] `ui/theme/`: Material 3, светлая + тёмная палитра
      (зелёная акцентная тема под «змею»), `Type.kt` по умолчанию.
- [ ] `MainActivity`: `setContent { ... }`, навигация через
      `sealed class Screen { Menu, Game, HowTo, GameOver(val score: Int, val isNewRecord: Boolean) }`
      + `remember { mutableStateOf(Screen.Menu) }` (без Jetpack Navigation
      — экранов всего 4).
- [ ] `MainMenuScreen`: заголовок «Змейка», рекорд, кнопки «Играть»
      (Fill, primary), «Как играть» (Tonal), версия/подпись.
- [ ] `HowToScreen`: правила (свайп/стрелки, еда, столкновения, скорость),
      кнопка «Назад».
**Критерий завершения:** приложение запускается, экраны переключаются,
тёмная тема работает.
**Критерий завершения:** приложение запускается, экраны переключаются,
тёмная тема работает.

---

## Этап 4. Игровой движок (SnakeGame.kt)
**Цель:** вся игровая логика работает без Android и покрывается тестами.

- [ ] `core/model/Point.kt` (data class), `Direction.kt` (enum:
      UP/DOWN/LEFT/RIGHT, свойства `dx/dy`, `opposite()`).
- [ ] `SnakeGame`: сетка (напр. 15×21), `snake: List<Point>`, `direction`,
      `food: Point`, `score`, `isGameOver`, `speedMs`.
- [ ] `step()`: сдвиг головы, рост при еде, столкновения (стена/хвост),
      новый `randomFood()` вне змеи, `speedMs` −5 мс на еду (мин. 90).
- [ ] `changeDirection()`: игнорировать разворот на 180° и более одного
      поворота за один тик.
- [ ] Юнит-тесты (`test/`): рост, еда, стена, хвост, разворот, ускорение,
      `isGameOver`, счёт, распределение еды без наложения на змею
      (seed'ed `Random` для воспроизводимости).
**Критерий завершения:** ≥ 15 тестов, покрытие ключевых ветвей 100 %,
`./gradlew :app:testDebugUnitTest` зелёное.

---

## Этап 5. Игровой экран
**Цель:** играется «как змейка».

- [ ] `GameViewModel : AndroidViewModel` + `SnakeGame` из модуля `core`;
      `MutableStateFlow<GameUiState>`.
- [ ] Тикер: `Handler(Looper.getMainLooper())` + `postDelayed` с
      динамическим интервалом из `speedMs`; старт/пауза/остановка;
      `onCleared()` — гарантированное удаление задержанного сообщения.
- [ ] `GameScreen`:
  - Canvas/Compose-рендер: змея (rounded-rect сегменты, голова),
    еда (круг/«яблоко»), сетка фона; `BoxWithConstraints` для размера;
  - жесты: `detectVerticalSwipeGestures` + `detectHorizontalSwipeGestures`
    → `changeDirection`;
  - `onKeyDown`-эмуляция для клавиатурного ввода (опционально);
  - кнопка «Пауза», счёт и рекорд сверху.
- [ ] Переход в `Screen.GameOver(score, isNewRecord)` при `isGameOver`;
      запись рекорда через `ScoreStorage`.
**Критерий завершения:** полная игровая сессия на устройстве/эмуляторе;
пауза; рекорд обновляется; без утечек (LeakCanary-проверка намеренно —
через лог памяти при 10 сессиях).

---

## Этап 6. Экран «Игра окончена», полировка, релиз-сборка
- [ ] `GameOverScreen`: счёт, «Новый рекорд!» (условно), кнопки
      «Ещё раз» / «В меню»; анимация появления.
- [ ] Полировка: звуковые эффекты (опц.), вибрация при смерти,
      splash-экран (Compose `SplashScreen`), иконка приложения
      (adaptive icon), `strings.xml` — весь текст вынесен в ресурсы.
- [ ] `lint` без ошибок, `testDebugUnitTest` зелёное,
      `assembleRelease` + подпись debug-ключом для проверки в Play
      Internal (опц.), README.md: как собрать и запустить.
**Критерий завершения:** приложение готово к установке на реальное
устройство; список «что дальше» (дополнения: уровни, бонусы, мультиплеер).

---

## 3.1. Порядок и зависимости

```
Этап 0 ──► Этап 1 ──► Этап 2 ──► Этап 4 ──► Этап 5 ──► Этап 6
             └────────► Этап 3 ────────┘
```
Этапы 2 и 3 можно делать параллельно; Этап 4 не зависит от UI.

## 3.2. Оптимистичные сроки (1 разработчик, ~4 ч/день)

| Этап | Оценка |
|------|--------|
| 0    | 0.5 д  |
| 1    | 0.5 д  |
| 2    | 0.5 д  |
| 3    | 1 д    |
| 4    | 1 д    |
| 5    | 1–1.5 д |
| 6    | 0.5–1 д |
| **Итого** | **5–6 дней** |

## 3.3. Риски

| Риск | Митигация |
|------|-----------|
| Утечка памяти в тикере | `removeCallbacks` в `onCleared` + `onPause` |
| Двойной тик после resume | флаг `isRunning` + перезапуск тикера только из `onResume` |
| Разворот на себя в пределах одного тика | буферировать максимум 2 направления в очереди |
| Случайная еда «залипает» на змее | `randomFood()` с циклом/множеством свободных клеток |

## 4. Критерии приёмки (Definition of Done)

- [ ] Игра полностью playable на Android 7.0+ (API 24+).
- [ ] Рекорд сохраняется между запусками.
- [ ] ≥ 15 юнит-тестов на движок, все зелёные.
- [ ] Линтер без ошибок; предупреждения — не более 10.
- [ ] Тёмная тема, свайпы, пауза работают.
- [ ] README с инструкцией сборки.

