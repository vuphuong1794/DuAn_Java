package game.obj;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class HealthPack {
    private double x, y;
    private boolean isActive;

    public HealthPack(double x, double y) {
        this.x = x;
        this.y = y;
        this.isActive = true; // Vật phẩm khởi tạo là hoạt động
    }

    public void draw(Graphics2D g2) {
        if (isActive) {
            g2.setColor(Color.GREEN); // Màu sắc của vật phẩm
            g2.fill(new Rectangle2D.Double(x, y, 20, 20)); // Vẽ hình vuông đại diện cho vật phẩm
        }
    }

    public Area getShape() {
        return new Area(new Rectangle((int) x, (int) y, 20, 20)); // Hình dạng của vật phẩm
    }

    public boolean isActive() {
        return isActive;
    }

    public void collect() {
        isActive = false; // Đánh dấu là đã được thu thập
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
