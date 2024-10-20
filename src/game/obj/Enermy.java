package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Enermy {

    public Enermy(){
        this.image = new ImageIcon(getClass().getResource("/game/image/enermy.png")).getImage();
    }

    public static final double ENERMY_SIZE = 84;
    private double x;
    private double y;
    private final float speed = 0.3f;
    private float angle = 0;
    private final Image image;

    //thay đổi vị trí
    public void changeLocation(double x, double y){
        this.x = x;
        this.y = y;
    }


    //thay đổi hướng xoay
    public void changeAngle(float angle){
        if(angle < 0){
            angle = 359;
        } else if(angle > 359){
            angle = 0;
        }
        this.angle = angle;
    }

    public void draw(Graphics2D g2){
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();

        g2.drawImage(image, 0, 0, null);
        g2.setTransform(oldTransform);
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
}
