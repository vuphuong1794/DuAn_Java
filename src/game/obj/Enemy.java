package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.Random;
import java.awt.geom.Path2D;

public class Enemy extends HpRender  {
    public static final double ENEMY_SIZE = 84;
    private double x, y;
    private static final float ENEMY_SPEED = 0.3f;
    private float angle = 0;
    private static final int NUM_IMAGES = 7;
    private final Image image;
    private final Area enemyShape;
    private boolean hasEnteredScreen = false;
    private boolean isActive = true;  // Thêm biến để kiểm soát trạng thái hoạt động
    private double targetX, targetY;  // Vị trí mục tiêu mà enemy sẽ di chuyển đến
    private static final float ROTATION_SPEED = 2.0f; // Tốc độ xoay của enemy
    private static final int MIN_DISTANCE = 150; // Khoảng cách tối thiểu với player

    public Enemy()  {
        // Them HP cho Enemy
        super( new HP(20,20));
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

    public void updateMovement(double playerX, double playerY, float playerAngle, float playerSpeed) {
        // Tính khoảng cách hiện tại đến player
        double distanceToPlayer = Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerY - y, 2));

        // Tính góc đến player
        double dx = playerX - x;
        double dy = playerY - y;
        double angleToPlayer = Math.toDegrees(Math.atan2(dy, dx));

        // Chuẩn hóa góc về khoảng 0-360
        if (angleToPlayer < 0) {
            angleToPlayer += 360;
        }

        // Tính góc xoay ngắn nhất
        double angleDifference = angleToPlayer - angle;
        if (angleDifference > 180) {
            angleDifference -= 360;
        } else if (angleDifference < -180) {
            angleDifference += 360;
        }

        // Xoay enemy
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

        // Di chuyển enemy
        x += Math.cos(Math.toRadians(angle)) * currentSpeed;
        y += Math.sin(Math.toRadians(angle)) * currentSpeed;
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
        Shape shape = getShape();

        // Gan gia tri hp cho Hprender
        hpRender(g2, shape, y);
        g2.setTransform(oldTransform);

        //test
        //g2.setColor(Color.red);
        //g2.draw(shape.getBounds2D());
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


    //===================================GETSHAPE CAN DUOC SUA LAI+++++++++++++++++++++++++++++++++++++++++++++
    public Area getShape() {
        // Get the width and height of the image
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        // Define the bounding area of the image centered at (0,0)
        Rectangle imageRect = new Rectangle(-width / 2, -height / 2, width, height);

        // Create a transformation for the enemy's position and angle
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y ); // Move to enemy's center
        afx.rotate(Math.toRadians(angle), 0, 0);  // Rotate around center

        // Return the transformed shape of the image area
        return new Area(afx.createTransformedShape(imageRect));
    }

    public boolean check(int width, int height) {
        // Define a buffer to allow enemies to stay in the game if they are close to the edges
        int buffer = 50; // Increase this buffer if needed for testing

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

}