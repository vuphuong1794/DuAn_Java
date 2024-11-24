package game.obj.item;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Item {
    public static final int ITEM_SIZE = 30;
    private double x;
    private double y;
    private Shape shape;
    private final BufferedImage image; // Hình ảnh cho item
    private boolean isCollected = false;
    private int type; // 0: Đạn thường, 1: Đạn lớn

    public Item(double x, double y) {
        this.x = x;
        this.y = y;
        this.type = type;
        // Tạo hình dạng cho item (hình tròn)
        shape = new Ellipse2D.Double(x, y, ITEM_SIZE, ITEM_SIZE);

        // Load hình ảnh dựa vào loại item
        String imagePath = "/game/image/BulletCircle.png";
        image = loadImage(imagePath);
    }

    private BufferedImage loadImage(String path) {
        try {
            Image img = new ImageIcon(getClass().getResource(path)).getImage();
            // Chuyển đổi Image thành BufferedImage
            BufferedImage bufferedImage = new BufferedImage(
                    img.getWidth(null),
                    img.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB
            );

            // Vẽ Image lên BufferedImage
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();

            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void draw(Graphics2D g2) {
        if (!isCollected) {
            if (image != null) {
                g2.drawImage(image, (int) x, (int) y, ITEM_SIZE, ITEM_SIZE, null);
            } else {
                // Fallback nếu không load được hình
                g2.setColor(type == 0 ? Color.YELLOW : Color.ORANGE);
                g2.fill(shape);
            }
        }
    }

    public Shape getShape() {
        return shape;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        isCollected = true;
    }

    public int getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}