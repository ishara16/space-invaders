/**
 * The high-level state the game can be in. Drives what gets drawn each
 * frame and how key input is interpreted, replacing the original project's
 * single "gameOver" boolean.
 */
public enum GameState {
    MENU,
    PLAYING,
    PAUSED,
    SETTINGS,
    GAME_OVER
}
