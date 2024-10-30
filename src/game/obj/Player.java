package game.obj;
import java.awt.geom.Path2D;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import javax.swing.ImageIcon;

public class Player extends HpRender {

    // Kích thước của nhân vật
    public static final double PLAYER_SIZE = 100;

    // Vị trí x, y của nhân vật trên màn hình
    private double x, y;

    // Tốc độ của nhân vật
    private float speed = 0f;
    private static final float MAX_SPEED = 1f;

    // Góc xoay của nhân vật (tính bằng độ)
    private float angle = 0f;

    private final Area playerShap;

    // Hình ảnh của nhân vật
    private final Image image;

    // Biến kiểm tra xem nhân vật có đang tăng tốc không
    private boolean speedUp;

    private boolean alive=true;

    // Constructor: Khởi tạo đối tượng Player và tải hình ảnh từ thư mục resources
    public Player() {
        super(new HP(50,50));
        this.image = loadImage("/game/image/CharacterPlayer.png");
        
    Path2D p = new Path2D.Double();
    p.moveTo(0, 15);
    p.lineTo(20, 5);
    p.lineTo(PLAYER_SIZE + 15, PLAYER_SIZE / 2);
    p.lineTo(20, PLAYER_SIZE - 5);
    p.lineTo(0, PLAYER_SIZE - 15);

    playerShap = new Area(p);
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

        // Move to the player's center and rotate around it
        g2.translate(x + PLAYER_SIZE / 2, y + PLAYER_SIZE / 2);
        g2.rotate(Math.toRadians(angle));

        // Draw the player image centered at (-width / 2, -height / 2)
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        g2.drawImage(image, -width / 2, -height / 2, null);

        hpRender(g2, getShape(), PLAYER_SIZE);

        // Restore the original transformation
        g2.setTransform(oldTransform);
    }

    // Method to create the player's original shape without scaling on rotation
    public Area getShape() {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        // Create a centered rectangle for the image bounds
        Rectangle rectangle = new Rectangle(-width / 2, -height / 2, width, height);

        // Apply rotation without changing position
        AffineTransform afx = new AffineTransform();
        afx.rotate(Math.toRadians(angle), 0, 0);  // Rotate around the center

        // Return the rotated shape area
        return new Area(afx.createTransformedShape(rectangle));
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

    public float getSpeed() {
        return speed;
    }
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void reset() {
        alive = true;
        resetHP();
        angle = 0;
        speed = 0;
    }


}