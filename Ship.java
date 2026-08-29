import java.awt.Image;

/** The player-controlled ship. Knows how to move left/right within board bounds. */
public class Ship extends Entity {
    private final int velocityX;

    public Ship(int x, int y, int width, int height, Image img, int velocityX) {
        super(x, y, width, height, img);
        this.velocityX = velocityX;
    }

    /** Moves left by one step, unless that would go past minX (left wall). */
    public void moveLeft(int minX) {
        if (x - velocityX >= minX) {
            x -= velocityX;
        }
    }

    /** Moves right by one step, unless that would go past maxX (right wall). */
    public void moveRight(int maxX) {
        if (x + velocityX + width <= maxX) {
            x += velocityX;
        }
    }

    public int getVelocityX() {
        return velocityX;
    }
}
