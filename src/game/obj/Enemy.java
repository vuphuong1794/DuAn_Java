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
    private final Area enemyShap;
    private boolean hasEnteredScreen = false; // Thêm biến để theo dõi trạng thái

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
        enemyShap = new Area(p);
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
        AffineTransform oldTransform = g2.getTransform();

        // Dịch chuyển đến vị trí trung tâm của Enemy và xoay quanh tâm
        g2.translate(x + ENEMY_SIZE / 2, y + ENEMY_SIZE / 2);
        g2.rotate(Math.toRadians(angle));

        // Vẽ hình ảnh Enemy tại (-width / 2, -height / 2) để căn giữa
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        g2.drawImage(image, -width / 2, -height / 2, null);


        // Khôi phục lại transform ban đầu
        g2.setTransform(oldTransform);
    }

    public Area getShape() {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        // Define the bounding area of the image centered at (0,0)
        Rectangle imageRect = new Rectangle(-width / 2, -height / 2, width, height);

        // Create a transformation for the enemy's position and angle
        AffineTransform afx = new AffineTransform();
        afx.translate(x + ENEMY_SIZE / 2, y + ENEMY_SIZE / 2); // Move to enemy's center
        afx.rotate(Math.toRadians(angle), 0, 0);  // Rotate around center

        // Return the transformed shape of the image area
        return new Area(afx.createTransformedShape(imageRect));
    }

    public boolean check(int width, int height) {
        // Nếu enemy chưa từng vào màn hình, luôn trả về true
        if (!hasEnteredScreen) {
            return true;
        }

        // Chỉ kiểm tra biên khi enemy đã từng vào màn hình
        Rectangle size = getShape().getBounds();
        boolean isOutOfBounds = x <= -size.getWidth() ||
                y <= -size.getHeight() ||
                x >= width + size.getWidth() ||
                y >= height + size.getHeight();

        // Trả về false nếu enemy đã ra khỏi màn hình sau khi đã vào
        return !isOutOfBounds;
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
}