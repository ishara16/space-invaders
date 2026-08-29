import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * Main game panel. Owns the game loop timer, the current GameState, and all
 * entities on screen. Responsibilities are split out into:
 *   - Entity/Ship/Alien/Bullet: what each object IS and how it moves/draws
 *   - GameState: which screen the game is currently showing
 *   - SoundManager: sound effects / music / mute
 * This class is left to coordinate: input handling, collision resolution,
 * and screen drawing per state.
 */
public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    // board
    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    // ship
    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * columns / 2 - tileSize;
    int shipY = tileSize * rows - tileSize * 2;
    int shipVelocityX = tileSize;
    Ship ship;

    // aliens
    ArrayList<Alien> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;
    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    // bullets
    ArrayList<Bullet> bulletArray;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10;

    Timer gameLoop;
    int score = 0;
    Random random = new Random();

    // ---- state machine ----
    GameState state = GameState.MENU;

    // ---- sound file names (drop matching .wav files next to the .class files) ----
    static final String SHOOT_SOUND = "shoot.wav";
    static final String EXPLOSION_SOUND = "explosion.wav";
    static final String GAME_OVER_SOUND = "gameover.wav";
    static final String BACKGROUND_MUSIC = "background.wav";

    SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // load images
        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Ship(shipX, shipY, shipWidth, shipHeight, shipImg, shipVelocityX);
        alienArray = new ArrayList<Alien>();
        bulletArray = new ArrayList<Bullet>();

        // Game loop always runs at 60fps; move() itself checks `state` and
        // becomes a no-op on any screen other than PLAYING, so menus/pause
        // don't need a separate timer.
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        // No music on the menu screen until the player actually starts a run.
    }

    // ================= DRAWING =================

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (state) {
            case MENU -> drawMenu(g);
            case SETTINGS -> drawSettings(g);
            case PLAYING -> drawGame(g);
            case PAUSED -> {
                drawGame(g);
                drawPauseOverlay(g);
            }
            case GAME_OVER -> {
                drawGame(g);
                drawGameOverOverlay(g);
            }
        }
    }

    private void drawCentered(Graphics g, String text, int y, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        int x = (boardWidth - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private void drawMenu(Graphics g) {
        drawCentered(g, "SPACE INVADERS", boardHeight / 2 - 80, new Font("Arial", Font.BOLD, 36), Color.green);
        drawCentered(g, "Press ENTER to Start", boardHeight / 2, new Font("Arial", Font.PLAIN, 22), Color.white);
        drawCentered(g, "Press S for Settings", boardHeight / 2 + 40, new Font("Arial", Font.PLAIN, 22), Color.white);
        drawCentered(g, "Arrows to move   Space to shoot   P to pause",
                boardHeight - 40, new Font("Arial", Font.PLAIN, 14), Color.gray);
    }

    private void drawSettings(Graphics g) {
        drawCentered(g, "SETTINGS", boardHeight / 2 - 80, new Font("Arial", Font.BOLD, 32), Color.green);
        String soundLine = "M - Sound: " + (SoundManager.isMuted() ? "OFF" : "ON");
        drawCentered(g, soundLine, boardHeight / 2, new Font("Arial", Font.PLAIN, 22), Color.white);
        drawCentered(g, "Press ENTER or ESC to go back",
                boardHeight / 2 + 50, new Font("Arial", Font.PLAIN, 18), Color.white);
    }

    private void drawGame(Graphics g) {
        ship.draw(g);

        for (Alien alien : alienArray) {
            alien.draw(g);
        }

        for (Bullet bullet : bulletArray) {
            bullet.draw(g);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString(String.valueOf(score), 10, 35);

        // small mute indicator so the player always knows the current sound state
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(Color.gray);
        String muteText = SoundManager.isMuted() ? "Sound: OFF (M)" : "Sound: ON (M)";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(muteText, boardWidth - fm.stringWidth(muteText) - 10, 20);
    }

    private void drawPauseOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, boardWidth, boardHeight);
        drawCentered(g, "PAUSED", boardHeight / 2 - 40, new Font("Arial", Font.BOLD, 32), Color.white);
        drawCentered(g, "P to Resume   ESC for Menu",
                boardHeight / 2 + 10, new Font("Arial", Font.PLAIN, 18), Color.white);
    }

    private void drawGameOverOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, boardWidth, boardHeight);
        drawCentered(g, "GAME OVER", boardHeight / 2 - 60, new Font("Arial", Font.BOLD, 32), Color.red);
        drawCentered(g, "Score: " + score, boardHeight / 2 - 20, new Font("Arial", Font.PLAIN, 22), Color.white);
        drawCentered(g, "ENTER to Restart   ESC for Menu",
                boardHeight / 2 + 20, new Font("Arial", Font.PLAIN, 18), Color.white);
    }

    // ================= GAME LOGIC =================

    public void move() {
        if (state != GameState.PLAYING) {
            return; // menus / pause / game-over: nothing to simulate
        }

        // aliens
        for (Alien alien : alienArray) {
            if (!alien.isActive()) {
                continue;
            }
            alien.moveBy(alienVelocityX, 0);

            if (alien.getX() + alien.getWidth() >= boardWidth || alien.getX() <= 0) {
                alienVelocityX *= -1;
                alien.moveBy(alienVelocityX * 2, 0);
                for (Alien a : alienArray) {
                    a.moveBy(0, alienHeight);
                }
            }

            if (alien.getY() >= ship.getY()) {
                endGame();
            }
        }

        // bullets
        for (Bullet bullet : bulletArray) {
            bullet.move();
            for (Alien alien : alienArray) {
                if (bullet.isActive() && alien.isActive() && bullet.collidesWith(alien)) {
                    bullet.setActive(false);
                    alien.setActive(false);
                    alienCount--;
                    score += 100;
                    SoundManager.play(EXPLOSION_SOUND);
                }
            }
        }

        while (!bulletArray.isEmpty() && (!bulletArray.get(0).isActive() || bulletArray.get(0).isOffScreen())) {
            bulletArray.remove(0);
        }

        if (alienCount == 0) {
            score += alienColumns * alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, columns / 2 - 2);
            alienRows = Math.min(alienRows + 1, rows - 6);
            alienArray.clear();
            bulletArray.clear();
            createAliens();
        }
    }

    private void endGame() {
        if (state != GameState.GAME_OVER) {
            SoundManager.stopMusic();
            SoundManager.play(GAME_OVER_SOUND);
        }
        state = GameState.GAME_OVER;
    }

    public void createAliens() {
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                Image img = alienImgArray.get(random.nextInt(alienImgArray.size()));
                alienArray.add(new Alien(alienX + c * alienWidth, alienY + r * alienHeight,
                        alienWidth, alienHeight, img));
            }
        }
        alienCount = alienArray.size();
    }

    /** Resets all game data and transitions into a fresh PLAYING run. */
    private void startNewGame() {
        ship.setX(shipX);
        bulletArray.clear();
        alienArray.clear();
        score = 0;
        alienColumns = 3;
        alienRows = 2;
        alienVelocityX = 1;
        createAliens();
        state = GameState.PLAYING;
        SoundManager.loopMusic(BACKGROUND_MUSIC);
    }

    private void returnToMenu() {
        SoundManager.stopMusic();
        state = GameState.MENU;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (state) {
            case MENU -> {
                if (key == KeyEvent.VK_ENTER) {
                    startNewGame();
                } else if (key == KeyEvent.VK_S) {
                    state = GameState.SETTINGS;
                } else if (key == KeyEvent.VK_M) {
                    SoundManager.toggleMuted();
                }
            }
            case SETTINGS -> {
                if (key == KeyEvent.VK_M) {
                    SoundManager.toggleMuted();
                } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_ESCAPE) {
                    state = GameState.MENU;
                }
            }
            case PLAYING -> {
                if (key == KeyEvent.VK_P) {
                    state = GameState.PAUSED;
                } else if (key == KeyEvent.VK_M) {
                    SoundManager.toggleMuted();
                } else if (key == KeyEvent.VK_LEFT) {
                    ship.moveLeft(0);
                } else if (key == KeyEvent.VK_RIGHT) {
                    ship.moveRight(boardWidth);
                } else if (key == KeyEvent.VK_SPACE) {
                    bulletArray.add(new Bullet(ship.getX() + shipWidth * 15 / 32, ship.getY(),
                            bulletWidth, bulletHeight, bulletVelocityY, Color.white));
                    SoundManager.play(SHOOT_SOUND);
                }
            }
            case PAUSED -> {
                if (key == KeyEvent.VK_P) {
                    state = GameState.PLAYING;
                } else if (key == KeyEvent.VK_ESCAPE) {
                    returnToMenu();
                } else if (key == KeyEvent.VK_M) {
                    SoundManager.toggleMuted();
                }
            }
            case GAME_OVER -> {
                if (key == KeyEvent.VK_ENTER) {
                    startNewGame();
                } else if (key == KeyEvent.VK_ESCAPE) {
                    returnToMenu();
                }
            }
        }
    }
}
