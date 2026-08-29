import java.awt.Image;

/** An enemy alien. Formation movement (left/right sweep, drop down) is driven by the game loop. */
public class Alien extends Entity {
    public Alien(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }

    /** Shifts this alien's position by a delta. Used for the sideways sweep and the drop-down step. */
    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }
}
