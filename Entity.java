import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * Base class for anything drawn and moved on the game board.
 * Centralizes position, size, sprite image, and an "active" flag
 * (alive for aliens, not-yet-consumed for bullets) so each subclass
 * only needs to add the behavior specific to that entity type.
 *
 * This replaces the original project's single reused "Block" class,
 * which mixed ship/alien/bullet concerns together with two loosely-named
 * booleans (`alive`, `used`) that meant different things depending on
 * which kind of object it was attached to.
 */
public abstract class Entity {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image img;

    // false = destroyed (alien), consumed (bullet), or otherwise no longer
    // drawn or collidable. A single, clearly-named flag for every subclass.
    protected boolean active = true;

    public Entity(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    /** Default rendering: draw the sprite image at the entity's position. Subclasses may override. */
    public void draw(Graphics g) {
        if (active && img != null) {
            g.drawImage(img, x, y, width, height, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /** Simple AABB collision check shared by every entity pair (bullet-alien, etc). */
    public boolean collidesWith(Entity other) {
        return this.active && other.active && getBounds().intersects(other.getBounds());
    }

    // ---- getters / setters ----
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
