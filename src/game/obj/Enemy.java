package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class Enemy {
    public static final double ENEMY_SIZE = 84;
    private double x, y;
    private final float speed = 0.3f;
    private float angle = 0;
    private static final int NUM_IMAGES = 7; // Số lượng hình ảnh Z1 đến Z7
    private final Image image;

    public Enemy() {
        // Tạo một số ngẫu nhiên từ 1 đến 7
        Random random = new Random();
        int imageNumber = random.nextInt(NUM_IMAGES) + 1;

        // Lấy đường dẫn hình ảnh ngẫu nhiên dựa trên số ngẫu nhiên
        String imagePath = "/game/image/Z" + imageNumber + ".png";
        System.out.println(imagePath);
        this.image = new ImageIcon(getClass().getResource(imagePath)).getImage();
    }
    // Thay đổi vị trí
    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Thay đổi hướng xoay
    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    // Cập nhật vị trí dựa trên góc
    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    // Vẽ đối tượng kẻ thù
    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle), ENEMY_SIZE / 2, ENEMY_SIZE / 2);
        g2.drawImage(image, tran, null);
        g2.setTransform(oldTransform);
    }

    // Lấy giá trị tọa độ x
    public double getX() {
        return x;
    }

    // Lấy giá trị tọa độ y
    public double getY() {
        return y;
    }

    // Lấy góc hiện tại của kẻ thù
    public float getAngle() {
        return angle;
    }
}
