package game.obj;
import java.awt.geom.Path2D;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import javax.swing.ImageIcon;

public class Player extends HpRender {
    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 740;
    public static final double PLAYER_SIZE = 64;

    private double x, y;
    private float speed = 0f;
    private static final float MAX_SPEED = 1f;
    private float angle = 0f;
    private final Area playerShap;
    private final Image image;
    private boolean alive = true;

    public Player() {
        super(new HP(50, 50));
        this.image = loadImage("/game/image/CharacterPlayer.png");

        Path2D p = new Path2D.Double();
        double halfSize = PLAYER_SIZE / 2;
        // Start from center and create a box around it
        p.moveTo(-halfSize, -halfSize);
        p.lineTo(halfSize, -halfSize);
        p.lineTo(halfSize, halfSize);
        p.lineTo(-halfSize, halfSize);
        p.closePath();

        playerShap = new Area(p);
    }

    private Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();

        double centerX = x + PLAYER_SIZE / 2;
        double centerY = y + PLAYER_SIZE / 2;

        // HP bar
        Area currentShape = getShape();
        Rectangle bounds = currentShape.getBounds();
        hpRender(g2, currentShape, bounds.y - 20);

        // player sprite
        g2.translate(centerX, centerY);
        g2.rotate(Math.toRadians(angle));
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        g2.drawImage(image, -width / 2, -height / 2, null);

        g2.setTransform(oldTransform);

        // test
        g2.setColor(new Color(12, 173, 84));
        g2.draw(currentShape);
    }

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

    public void update() {
        double newX = x + Math.cos(Math.toRadians(angle)) * speed;
        double newY = y + Math.sin(Math.toRadians(angle)) * speed;
        changeLocation(newX, newY);
    }

    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    public Area getShape() {
        AffineTransform afx = new AffineTransform();

        double centerX = x + PLAYER_SIZE / 2;
        double centerY = y + PLAYER_SIZE / 2;

        afx.translate(centerX, centerY);
        afx.rotate(Math.toRadians(angle));
        return new Area(afx.createTransformedShape(playerShap));
    }

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