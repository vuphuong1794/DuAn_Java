package game.obj;
import java.awt.*;
import java.awt.geom.AffineTransform;


public class Explosion {
    private double x, y;
    private Image image;
    private int duration; // Duration in milliseconds
    private int elapsedTime; // Tracks how long the explosion has been active

    public Explosion(double x, double y, Image image, int duration) {
        System.out.println("Explodsion created.");
        this.x = x;
        this.y = y;
        this.image = image;
        this.duration = duration;
        this.elapsedTime = 0;
    }

    public void update(int deltaTime) {

        elapsedTime += deltaTime;
    }

    public boolean isExpired() {
        return elapsedTime >= duration;
    }

    public void render(Graphics2D g2, Bullet bullet) {
        if (image != null) {
            AffineTransform oldTransform = g2.getTransform();
            g2.drawImage(image, (int) x - 50, (int) y - 50, 100, 100, null); // Centered and scaled
            g2.translate(bullet.getX(), bullet.getY());
            g2.setTransform(oldTransform);
        }
    }
}
