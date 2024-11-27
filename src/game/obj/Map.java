package game.obj;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Area;




public class Map {
    private List<Rectangle> walls;

    public Map() {
        walls = new ArrayList<>();

        createWallWidth(0, 200, 100, 3);
        createWallHeight(830, 0, 100, 4);
    }

    // Phương thức lấy danh sách tường
    public List<Rectangle> getWalls() {
        return walls;
    }
    public void createWallWidth(int startX, int startY, int length, int numberOfWall){
        for (int i=1;i<=numberOfWall;i++){
            walls.add(new Rectangle(startX, startY, length, length));  // Tường ngang 1
            startX =i*length;
        }

    }

    public void createWallHeight(int startX, int startY, int length, int numberOfWall) {
        for (int i = 1; i <= numberOfWall; i++) {
            walls.add(new Rectangle(startX, startY, length, length));  // Tường ngang 1
            startY = i * length;
        }
    }
    // Vẽ bản đồ và các tường
    public void draw(Graphics2D g2) {
        g2.setColor(new java.awt.Color(255, 0, 0, 100)); // Màu đỏ với alpha = 0 (hoàn toàn trong suốt)
        for (Rectangle wall : walls) {
            g2.fill(wall);
        }
    }

    // Kiểm tra va chạm giữa đạn và tường
    public boolean isBulletCollidingWithWall(Bullet bullet) {
        for (Rectangle wall : walls) {
            if (bullet.getShape().intersects(wall)) {
                return true; // Đạn va chạm với tường
            }
        }
        return false; // Không có va chạm
    }

    //kiểm tra va chạm
    public String checkCollision(Player player) {
        StringBuilder bounds = new StringBuilder();
        // Kiểm tra nếu có va chạm chung với các bức tường
        for (Rectangle wall : walls) {

            if (player.getShape().getBounds().intersects(wall)) {

                // Kiểm tra va chạm trên
                if (player.getY() - 20 == wall.getY() + wall.getHeight()) {
                    bounds.append("up");
                }
                // Kiểm tra va chạm dưới
                if (player.getY() + 20 == wall.getY()) {
                    bounds.append("down");
                }
                // Kiểm tra va chạm trái
                if (player.getX() - 20 == wall.getX() + wall.getWidth()) {
                    bounds.append("left");
                }
                // Kiểm tra va chạm phải
                if (player.getX() + 20 == wall.getX()) {
                    bounds.append("right");
                }

                return bounds.toString(); // Trả về hướng va chạm
            }
        }
        return ""; // Không có va chạm
    }

    public String isEnemyCollidingWithWall(Enemy enemy) {
        StringBuilder bounds = new StringBuilder();
        double threshold = 1.0; // Allow for small inaccuracies in position comparison

        for (Rectangle wall : walls) {
            if (enemy.getShape().intersects(wall)) { // Check if enemy's shape intersects the wall
                // Check collision with the top of the wall
                if (Math.abs(enemy.getY()-20 - (wall.getY() + wall.getHeight())) < threshold) {
                    System.out.println("Colliding with wall up");
                    bounds.append("up,");
                }
                // Check collision with the bottom of the wall
                if (Math.abs(enemy.getY()+20 - wall.getY()) < threshold) {
                    System.out.println("Colliding with wall down");
                    bounds.append("down,");
                }
                // Check collision with the left of the wall
                if (Math.abs(enemy.getX()-20 - (wall.getX() + wall.getWidth())) < threshold) {
                    System.out.println("Colliding with wall left");
                    bounds.append("left,");
                }
                // Check collision with the right of the wall
                if (Math.abs(enemy.getX()+20 - wall.getX()) < threshold) {
                    System.out.println("Colliding with wall right");
                    bounds.append("right,");
                }
            }
        }

        // Remove the trailing comma for cleaner output
        if (bounds.length() > 0) {
            bounds.setLength(bounds.length() - 1); // Remove last comma
            System.out.println("Zombie collide with: " + bounds);
        }

        return bounds.toString(); // Return all collision directions
    }

}