package game.obj;
import java.awt.geom.Path2D;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 940;
    public static final double PLAYER_SIZE = 50;
    private String playerName = getPlayerName();

    // Vị trí x, y của nhân vật trên màn hình
    private double x, y;

    private List<Gun> inventory;    // List of guns the player owns
    private int currentGunIndex;    // Index of the currently equipped gun

    // Tốc độ của nhân vật
    private float speed = 0.5f;
    private static final float MAX_SPEED = 1f;

    // Góc xoay của nhân vật (tính bằng độ)
    private float angle = 0f;

    private Area playerShape;

    // Hình ảnh của nhân vật
    private final Image image;

    private boolean alive=true;

    public HpRender hpPlayer;
    // Constructor: Khởi tạo đối tượng Player và tải hình ảnh từ thư mục resources
    public Player() {
        hpPlayer=new HpRender(50); //Máu mặc định
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

    // Method to draw the player on the screen
    public void draw(Graphics2D g2) {
        // Save the current transformation state
        AffineTransform oldTransform = g2.getTransform();
        // Transform to the player's position and rotation
        g2.translate(x, y);
        // Draw the player's name above the character
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString(playerName, -(int)(PLAYER_SIZE / 2), -(int)(PLAYER_SIZE / 2)-10);
        g2.rotate(Math.toRadians(angle- 90));

        // Scale the player image
        double scaleFactor = 0.02; // Uniform scaling factor
        g2.scale(scaleFactor, scaleFactor);

        // Draw the player image centered at (-width / 2, -height / 2)
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        //System.out.println("Player position: " + -width / 2 +", "+ -height / 2);
        g2.drawImage(image, -width / 2, -height / 2, null);


        // Restore the original transformation state
        g2.setTransform(oldTransform);
        //drawBorder(g2); // Vẽ player
        drawHealth(g2);
    }
    public void drawBorder(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        // Draw a red rectangle around the player's hitbox
        g2.setColor(Color.RED);
        Shape shape = getShape(); // Get the player's shape (rotated hitbox)
        if (shape != null) {
            g2.draw(shape.getBounds2D()); // Draw the bounding rectangle of the shape
        }
        else {
            System.out.println("No shape found");
        }
        g2.scale(1, 1);

        // Restore the original transformation state
        // Render health bar or other overlays
        hpPlayer.hpRender(g2, getShape(),0, (int)(PLAYER_SIZE / 2));
        // Restore the original transformation state
        g2.setTransform(oldTransform);
    }

    void drawHealth(Graphics2D g2){
        AffineTransform oldTransform = g2.getTransform();
        // Draw a red rectangle around the player's hitbox
        g2.setColor(Color.RED);
        g2.scale(1, 1);

        // Restore the original transformation state
        // Render health bar or other overlays
        hpPlayer.hpRender(g2, getShape(),0, (int)(PLAYER_SIZE / 2));
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
        afx.translate(x, y );
        //afx.rotate(Math.toRadians(angle), 0, 0);// Xoay 1 độ angle với tâm là x, y

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

    public float getHeight(){
        return image.getHeight(null);
    }

    public float getWidth(){
        return image.getWidth(null);
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
        hpPlayer.resetHP();
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
