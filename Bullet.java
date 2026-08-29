import java.awt.Color;
import java.awt.Graphics;

/** A projectile fired by the player. Has no sprite - draws itself as a simple outlined rectangle. */
public class Bullet extends Entity {
    private final int velocityY;
    private final Color color;

    public Bullet(int x, int y, int width, int height, int velocityY, Color color) {
        super(x, y, width, height, null);
        this.velocityY = velocityY;
        this.color = color;
    }

    /** Advances the bullet by its vertical velocity. Call once per frame. */
    public void move() {
        y += velocityY;
    }

    @Override
    public void draw(Graphics g) {
        if (!active) {
            return;
        }
        g.setColor(color);
        g.drawRect(x, y, width, height);
    }

    /** True once the bullet has left the top of the board and can be cleaned up. */
    public boolean isOffScreen() {
        return y < 0;
    }
}
