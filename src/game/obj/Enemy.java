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
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle), ENEMY_SIZE / 2, ENEMY_SIZE / 2);
        g2.drawImage(image, tran, null);
        Shape shap = getShape();
        g2.setTransform(oldTransform);

        //test
        g2.setColor(Color.red);
        g2.draw(shap.getBounds2D());


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
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), ENEMY_SIZE/2, ENEMY_SIZE/2);
        return new Area(afx.createTransformedShape(enemyShap));
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
}