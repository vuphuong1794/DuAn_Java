package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.Random;
import java.awt.geom.Path2D;

public class Enemy {
    public static double ENEMY_SIZE = 50;
    private double x, y;
    private float ENEMY_SPEED = 0.3f;
    private float angle = 0;
    private static final int NUM_IMAGES = 11;
    private final Image image;
    private final Area enemyShape;
    private boolean hasEnteredScreen = false;
    private boolean isActive = true;  // Thêm biến để kiểm soát trạng thái hoạt động
    private double targetX, targetY;  // Vị trí mục tiêu mà enemy sẽ di chuyển đến
    private static final float ROTATION_SPEED = 2.0f; // Tốc độ xoay của enemy
    private static final int MIN_DISTANCE = 150; // Khoảng cách tối thiểu với player
    public HpRender hpEnemey;
    public int type;

    private boolean collideLeft;
    private boolean collideRight;
    private boolean collideUp;
    private boolean collideDown;

    public Enemy()  {

        Random random = new Random();
        int imageNumber = random.nextInt(NUM_IMAGES) + 1;
        String imagePath = "/game/image/Z" + imageNumber + ".png";
        this.image = loadImage(imagePath);

        if(imageNumber==6){
            type = 2;
            ENEMY_SPEED=0.3f;
            hpEnemey=new HpRender(35);
        }
        else if(imageNumber==7){
            type = 3;
            ENEMY_SPEED=0.35f;
            hpEnemey=new HpRender(50);
        }
        else if (imageNumber>=8 && imageNumber<=11){
            type = 4;
            ENEMY_SPEED=0.8f;
            hpEnemey=new HpRender(15);
        }
        else{
            type = 1;
            ENEMY_SPEED=0.3f;
            hpEnemey=new HpRender(20);
        }

        Path2D p = new Path2D.Double();
        p.moveTo(0, ENEMY_SIZE/2);
        p.lineTo(15, 10);
        p.lineTo(ENEMY_SIZE-5, 13);
        p.lineTo(ENEMY_SIZE+10, ENEMY_SIZE/2);
        p.lineTo(ENEMY_SIZE-5, ENEMY_SIZE-13);
        p.lineTo(15, ENEMY_SIZE-10);
        enemyShape = new Area(p);

        setCollideFalse();

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
    public void updateMovement(double playerX, double playerY, String collider) {
        //System.out.println("Enemy position: " + getX() + ", " + getY());

        // Tính khoảng cách hiện tại đến player
        double distanceToPlayer = Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerY - y, 2));

        // Tính góc đến player
        double dx = playerX - x;
        double dy = playerY - y;
        double angleToPlayer = Math.toDegrees(Math.atan2(dy, dx));

        // Lưu trữ vị trí hiện tại trước khi di chuyển
        double prevX = x;
        double prevY = y;

        // Di chuyển theo hướng
        x += Math.cos(Math.toRadians(angle)) * ENEMY_SPEED;
        y += Math.sin(Math.toRadians(angle)) * ENEMY_SPEED;

        // Tính góc xoay ngắn nhất
        //vd: với góc của player là 10, zombie là 350 thay vì phải quay 360 + 10 thì chỉ cần quay -20 độ
        double angleDifference = angleToPlayer - angle; //10 - 350 = -340
        if (angleDifference > 180) {
            angleDifference -= 360;
        } else if (angleDifference < -180) { //-340 < 180
            angleDifference += 360; //-340 + 360 = 20 => chỉ cần xoay 20 độ
        }

        // Xoay enemy mượt
        if (Math.abs(angleDifference) > ROTATION_SPEED) {
            if (angleDifference > 0) {
                angle += ROTATION_SPEED;
            } else {
                angle -= ROTATION_SPEED;
            }
        } else {
            angle = (float) angleToPlayer;
        }

        // Giữ góc trong khoảng 0-360
        if (angle < 0) {
            angle += 360;
        } else if (angle >= 360) {
            angle -= 360;
        }

        // Tính toán tốc độ di chuyển
        double currentSpeed = ENEMY_SPEED;

        // Nếu quá gần player, giảm tốc
        if (distanceToPlayer < MIN_DISTANCE) {
            currentSpeed *= (distanceToPlayer / MIN_DISTANCE);
        }

        //Check va chạm để di chuyển cho đúng
        // Kiểm tra va chạm chi tiết
        if (collider.contains("left") || collider.contains("right") ||
                collider.contains("up") || collider.contains("down")) {
            // Nếu va chạm, quay lại vị trí cũ
            x = prevX;
            y = prevY;

            // Thay đổi hướng để tránh va chạm
            if (collider.contains("left")) {
                // Va chạm bên trái, quay sang phải
                angle = (angle + 90) % 360;
            } else if (collider.contains("right")) {
                // Va chạm bên phải, quay sang trái
                angle = (angle - 90 + 360) % 360;
            }

            if (collider.contains("up")) {
                // Va chạm phía trên, quay xuống dưới
                angle = (angle + 90) % 360;
            } else if (collider.contains("down")) {
                // Va chạm phía dưới, quay lên trên
                angle = (angle - 90 + 360) % 360;
            }

            setCollideFalse();
            // Di chuyển trong hướng mới
            x += Math.cos(Math.toRadians(angle)) * currentSpeed;
            y += Math.sin(Math.toRadians(angle)) * currentSpeed;
        }
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

    public void draw(Graphics2D g2) {
        if (!isActive) return;  // Không vẽ nếu zombie không còn hoạt động

        AffineTransform oldTransform = g2.getTransform();
        // Dịch chuyển đến vị trí trung tâm của Enemy và xoay quanh tâm
        g2.translate(x, y);
        g2.rotate(Math.toRadians(angle));

        // Vẽ hình ảnh Enemy tại (-width / 2, -height / 2) để căn giữa
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        g2.drawImage(image, -width / 2, -height / 2, null);
        g2.rotate(Math.toRadians(-angle));
        Shape shape = getShape();
        // Gan gia tri hp cho Hprender
        hpEnemey.hpRender(g2, shape,x, y+10);
        g2.setTransform(oldTransform);
        //drawBorder(g2);
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
        Rectangle imageRect = new Rectangle(-(int)(ENEMY_SIZE/2), -(int)(ENEMY_SIZE/2), (int)ENEMY_SIZE, (int)ENEMY_SIZE);

        // Create a transformation for the enemy's position and angle
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y ); // Move to enemy's center

        // Return the transformed shape of the image area
        return new Area(afx.createTransformedShape(imageRect));
    }

    public void drawBorder(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.setColor(Color.RED);
        Shape shape = getShape(); // Get the player's shape (rotated hitbox)
        if (shape != null) {
            g2.draw(shape.getBounds2D()); // Draw the bounding rectangle of the shape
        }
        else {
            System.out.println("No shape found");
        }

        // Restore the original transformation state
        g2.setTransform(oldTransform);
    }

    public boolean check(int width, int height) {
        // Define a buffer to allow enemies to stay in the game if they are close to the edges
        int buffer = 50;

        // Nếu enemy chưa từng vào màn hình, luôn trả về true
        if (!hasEnteredScreen) {
            return true;
        }

        // Chỉ kiểm tra biên khi enemy đã từng vào màn hình
        Rectangle size = getShape().getBounds();

        // Check if the enemy is beyond the screen bounds plus the buffer
        boolean isOutOfBounds = x <= -size.getWidth() - buffer ||
                y < -size.getHeight() - buffer ||
                x > width + buffer ||
                y > height + buffer;

        // If out of bounds, return false to mark for removal
        return !isOutOfBounds;
    }

    void setCollideFalse(){
        collideRight=false;
        collideDown=false;
        collideLeft=false;
        collideUp=false;
    }

}