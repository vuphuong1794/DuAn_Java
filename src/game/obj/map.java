package game.obj;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Area;




public class Map {
    private List<Rectangle> walls;

    public Map() {
        walls = new ArrayList<>();

        // Tạo các bức tường cố định
        walls.add(new Rectangle(0, 160, 490, 88));  // Tường ngang 1
        walls.add(new Rectangle(330, 0, 160, 240)); // Tường dọc 1
        walls.add(new Rectangle(735, 0, 30, 240)); // Tường dọc 2
        walls.add(new Rectangle(735, 220, 605,20)); // Tường ngang 2
        walls.add(new Rectangle(925, 240, 45, 290)); // Tường dọc 3
        walls.add(new Rectangle(920, 715, 45, 290)); // Tường dọc 4
    }

    // Phương thức lấy danh sách tường
    public List<Rectangle> getWalls() {
        return walls;
    }

    // Vẽ bản đồ và các tường
    public void draw(Graphics2D g2) {
        g2.setColor(new java.awt.Color(255, 0, 0, 0)); // Màu đỏ với alpha = 0 (hoàn toàn trong suốt)
        for (Rectangle wall : walls) {
            g2.fill(wall);
        }
    }
    //kiểm tra va chạm
   public String checkCollision(Player player) {
    // Kiểm tra nếu có va chạm chung với các bức tường
        for (Rectangle wall : walls) {
            if (player.getShape().getBounds().intersects(wall)) {
                // Kiểm tra va chạm trên
                if (player.getY() == wall.getY() + wall.getHeight()) {
                    System.out.println("up "+player.getX()+" "+player.getY()+" "+wall.getX()+" "+wall.getY());
                    return "up";  // Va chạm từ trên
                }
                if (player.getY() == wall.getY()) {
                    System.out.println("down "+player.getX()+" "+player.getY()+" "+wall.getX()+" "+wall.getY());
                    return "down";  // Va chạm từ trên
                }
                if (player.getX() == wall.getX()+wall.getWidth()) {
                    System.out.println("left "+player.getX()+" "+player.getY()+" "+wall.getX()+" "+wall.getY());
                    return "left";  // Va chạm từ trên
                }
                if (player.getX() == wall.getX()) {
                    System.out.println("right "+player.getX()+" "+player.getY()+" "+wall.getX()+" "+wall.getY());
                    return "right";  // Va chạm từ trên
                }
            }
        }
        return "Không có va chạm";  // Nếu không có va chạm
    }
}