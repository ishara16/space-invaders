# Space Invaders

A classic Space Invaders arcade game built in Java with Swing. Shoot down waves of aliens before they reach your ship. Each wave you clear brings a bigger formation.

## Features

- **Game screens**: Main Menu, Settings, Pause and Game Over, driven by a simple state machine
- **Progressive difficulty**: the alien grid grows by a row and a column after each cleared wave
- **Colourful enemies**: aliens get a random sprite out of 4 colours
- **Scoring**: 100 points per alien, plus a bonus for clearing a wave
- **Sound**: shoot, explosion and game-over effects, looping background music, and a mute toggle

## Controls

| Screen    | Key       | Action             |
|-----------|-----------|--------------------|
| Menu      | `Enter`   | Start game         |
|           | `S`       | Open settings      |
| Playing   | `←` / `→` | Move ship          |
|           | `Space`   | Shoot              |
|           | `P`       | Pause              |
| Paused    | `P`       | Resume             |
|           | `Esc`     | Back to menu       |
| Settings  | `Enter` / `Esc` | Back to menu |
| Game Over | `Enter`   | Restart            |
|           | `Esc`     | Back to menu       |
| Most screens | `M`    | Toggle sound on/off |

## Requirements

- Java **14 or newer** (the code uses `switch ->` expressions)

## How to Run

From the project folder:

```bash
javac *.java
java App
```

> Run from the project folder. The game loads the images and `.wav` files from the same folder as the compiled classes.

## Project Structure

```
├── App.java            # Entry point: creates the window
├── SpaceInvaders.java  # Game panel: game loop, input, collisions, drawing
├── GameState.java      # Enum: MENU, PLAYING, PAUSED, SETTINGS, GAME_OVER
├── Entity.java         # Abstract base: position, sprite, collision (AABB)
├── Ship.java           # Player ship, movement kept inside the board edges
├── Alien.java          # Enemy alien, moves as part of the formation
├── Bullet.java         # Player projectile
├── SoundManager.java   # Sound effects, looping music, mute
├── *.png               # Ship and alien sprites
└── *.wav               # Sound effects and background music
```

## How It Works

- **Game loop**: a Swing `Timer` fires about 60 times per second. Each tick calls `move()` and then `repaint()`.
- **State machine**: `GameState` decides what gets drawn and how key presses are handled, so menus and pause don't need extra flags.
- **Entities**: `Ship`, `Alien` and `Bullet` extend `Entity`, which holds the shared position, drawing and rectangle collision logic.
- **Sound**: `SoundManager` loads audio through the Java Sound API. If a sound file is missing, it skips the sound instead of crashing.

## Future Improvements

- Aliens that shoot back
- Player lives and a health display
- A high score saved between sessions
- Smooth movement while a key is held down
- Faster aliens on later waves
- Packaging as a runnable `.jar`
