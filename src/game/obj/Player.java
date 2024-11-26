package game.obj;
import java.awt.geom.Path2D;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;

public class Player extends HpRender {

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 940;
    public static final double PLAYER_SIZE = 100;
    private String playerName = getPlayerName();

    // Vị trí x, y của nhân vật trên màn hình
    private double x, y;

    private List<Gun> inventory;    // List of guns the player owns
    private int currentGunIndex;    // Index of the currently equipped gun

    // Tốc độ của nhân vật
    private float speed = 0f;
    private static final float MAX_SPEED = 1f;

    // Góc xoay của nhân vật (tính bằng độ)
    private float angle = 0f;

    private Area playerShape;

    // Hình ảnh của nhân vật
    private final Image image;

    private boolean alive=true;

    // Constructor: Khởi tạo đối tượng Player và tải hình ảnh từ thư mục resources
    public Player() {
        super(new HP(50,50));
        this.image = loadImage("/game/image/PlayerShortGun.png");
        this.inventory = new ArrayList<>();
        this.currentGunIndex = 0; // No gun equipped initially

        Path2D p = new Path2D.Double();
        p.moveTo(50, 50);
        p.lineTo(20, 5);
        p.lineTo(PLAYER_SIZE + 15, PLAYER_SIZE / 2);
        p.lineTo(20, PLAYER_SIZE - 5);
        p.lineTo(0, PLAYER_SIZE - 15);

        playerShape = new Area(p);
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

    // Add a gun to the player's inventory
    public void addGun(Gun gun) {
        inventory.add(gun);

        // Automatically equip the first gun added
        if (currentGunIndex == -1) {
            currentGunIndex = 0;
        }
    }

    // Equip a gun by index (bind this to number keys)
    public void equipGunByIndex(int index) {
        if (index < 0 || index >= inventory.size()) {
            System.out.println("Invalid gun index: " + index);
            return;
        }

        currentGunIndex = index;
        System.out.println("Equipped: " + getCurrentGun().getName());
    }

    // Get the currently equipped gun
    public Gun getCurrentGun() {
        if (currentGunIndex >= 0 && currentGunIndex < inventory.size()) {
            return inventory.get(currentGunIndex);
        }
        return null; // No gun equipped
    }

    // Phương thức thay đổi vị trí của nhân vật
    public void changeLocation(double newX, double newY) {
        if (newX < 0) {
            this.x = 0;
        } else if (newX > GAME_WIDTH - PLAYER_SIZE) {
            this.x = GAME_WIDTH - PLAYER_SIZE;
        } else {
            this.x = newX;
        }

        if (newY < 0) {
            this.y = 0;
        } else if (newY > GAME_HEIGHT - PLAYER_SIZE) {
            this.y = GAME_HEIGHT - PLAYER_SIZE;
        } else {
            this.y = newY;
        }
    }

    // Phương thức cập nhật vị trí của nhân vật dựa trên góc và tốc độ
    public void update() {
        double newX = x + Math.cos(Math.toRadians(angle)) * speed;
        double newY = y + Math.sin(Math.toRadians(angle)) * speed;
        changeLocation(newX, newY);
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
        g2.translate(x, y);
        g2.rotate(Math.toRadians(angle-90));

        // Draw the player image centered at (-width / 2, -height / 2)
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        double scaleFactorX = 0.02; // Scale relative to reference size (10.0)
        double scaleFactorY = 0.02;
        g2.scale(scaleFactorX, scaleFactorY);
        g2.drawImage(image, -width / 2, -height / 2, null);

        // Vẽ tên người chơi
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString(playerName, -(int)(PLAYER_SIZE / 2), -(int)(PLAYER_SIZE / 2) - 10);  // Hiển thị tên ngay phía trên nhân vật

        hpRender(g2, getShape(), PLAYER_SIZE);

        // Restore the original transformation
        g2.setTransform(oldTransform);
    }

    public Area getShape() {
        // Tạo hitbox với kích thước phù hợp với sprite
        Rectangle rectangle = new Rectangle(
                -(int)(PLAYER_SIZE/2), // Căn giữa theo chiều ngang
                -(int)(PLAYER_SIZE/2), // Căn giữa theo chiều dọc
                (int)PLAYER_SIZE,      // Chiều rộng
                (int)PLAYER_SIZE       // Chiều cao
        );

        AffineTransform afx = new AffineTransform();
        // Dịch chuyển đến tâm của nhân vật
        afx.translate(x + PLAYER_SIZE/2, y + PLAYER_SIZE/2);
        afx.rotate(Math.toRadians(angle), 0, 0);// Xoay 1 độ angle với tâm là x, y

        // Create an Area from the transformed rectangle
        return new Area(afx.createTransformedShape(rectangle));
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
    public boolean isAlive() {
        return alive;
    }

    public float getSpeed() {
        return speed;
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

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public String getPlayerName() {
        return playerName != null ? playerName : "player";
    }
}
