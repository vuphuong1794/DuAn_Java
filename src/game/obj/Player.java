package game.obj;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

public class Player {

    // Kích thước của nhân vật
    public static final double PLAYER_SIZE = 100;

    // Vị trí x, y của nhân vật trên màn hình
    private double x, y;

    // Tốc độ của nhân vật
    private float speed = 0f;
    private static final float MAX_SPEED = 1f;

    // Góc xoay của nhân vật (tính bằng độ)
    private float angle = 0f;

    // Hình ảnh của nhân vật
    private final Image image;

    // Biến kiểm tra xem nhân vật có đang tăng tốc không
    private boolean speedUp;

    // Constructor: Khởi tạo đối tượng Player và tải hình ảnh từ thư mục resources
    public Player() {
        this.image = loadImage("/game/image/CharacterPlayer.png");
    }

    // Phương thức tải hình ảnh và xử lý lỗi nếu có
    private Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    // Phương thức thay đổi vị trí của nhân vật
    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Phương thức cập nhật vị trí của nhân vật dựa trên góc và tốc độ
    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    // Phương thức thay đổi góc xoay của nhân vật
    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    // Phương thức vẽ nhân vật lên màn hình
    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = g2.getTransform();
        tran.rotate(Math.toRadians(angle), PLAYER_SIZE / 2, PLAYER_SIZE / 2);
        g2.drawImage(image, tran, null);
        g2.setTransform(oldTransform);
    }

    // Phương thức tăng tốc nhân vật
    public void speedUp() {
        speedUp = true;
        speed = Math.min(speed + 0.02f, MAX_SPEED);
    }

    // Phương thức giảm tốc nhân vật
    public void speedDown() {
        speedUp = false;
        speed = Math.max(speed - 0.01f, 0f);
    }

    // Getters cho vị trí x, y và góc xoay
    public float getX() {
        return (float) x;
    }

    public float getY() {
        return (float) y;
    }

    public float getAngle() {
        return angle;
    }


}