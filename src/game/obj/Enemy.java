package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.Random;
import java.awt.geom.Path2D;

public class Enemy {
    public static final double ENEMY_SIZE = 84;
    private double x, y;
    private final float speed = 0.3f;
    private float angle = 0;
    private static final int NUM_IMAGES = 7;
    private final Image image;
    private final Area enemyShape;
    private boolean hasEnteredScreen = false;
    private boolean isActive = true;  // Thêm biến để kiểm soát trạng thái hoạt động

    public Enemy() {
        Random random = new Random();
        int imageNumber = random.nextInt(NUM_IMAGES) + 1;
        String imagePath = "/game/image/Z" + imageNumber + ".png";
        this.image = new ImageIcon(getClass().getResource(imagePath)).getImage();
        Path2D p = new Path2D.Double();
        p.moveTo(0, ENEMY_SIZE/2);
        p.lineTo(15, 10);
        p.lineTo(ENEMY_SIZE-5, 13);
        p.lineTo(ENEMY_SIZE+10, ENEMY_SIZE/2);
        p.lineTo(ENEMY_SIZE-5, ENEMY_SIZE-13);
        p.lineTo(15, ENEMY_SIZE-10);
        enemyShape = new Area(p);
    }

    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;

        // Kiểm tra xem enemy đã vào trong màn hình chưa
        Rectangle bounds = getShape().getBounds();
        if (!hasEnteredScreen &&
                x + bounds.width > 0 && x < bounds.width + ENEMY_SIZE &&
                y + bounds.height > 0 && y < bounds.height + ENEMY_SIZE) {
            hasEnteredScreen = true;
        }
    }

    public void draw(Graphics2D g2) {
        if (!isActive) return;  // Không vẽ nếu zombie không còn hoạt động

        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle), ENEMY_SIZE / 2, ENEMY_SIZE / 2);
        g2.drawImage(image, tran, null);
        Shape shape = getShape();
        g2.setTransform(oldTransform);

        //test
        g2.setColor(Color.red);
        g2.draw(shape.getBounds2D());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public Area getShape() {
        Rectangle rectangle = new Rectangle(0, 0, 50, 50);
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), 0, 0);
        return new Area(afx.createTransformedShape(rectangle));
    }

    public boolean check(int width, int height) {
        // Define a buffer to allow enemies to stay in the game if they are close to the edges
        int buffer = 50; // Increase this buffer if needed for testing

        // Get enemy bounds
        Rectangle size = getShape().getBounds();

        // Check if the enemy is beyond the screen bounds plus the buffer
        boolean isOutOfBounds = x <= -size.getWidth() - buffer ||
                y < -size.getHeight() - buffer ||
                x > width + buffer ||
                y > height + buffer;

        // If out of bounds, return false to mark for removal
        return !isOutOfBounds;
    }

}