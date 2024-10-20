package game.obj;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

public class Player {

    public Player(){
        this.image = new ImageIcon(getClass().getResource("/game/image/Player.png")).getImage();
    }

    public static final double PLAYER_SIZE = 84;
    private double x; //vị trí x nhân vật
    private double y; // vị trí y nhân vật
    private float angle = 0f;
    private final Image image; //ảnh nhân vật

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
        AffineTransform tran = g2.getTransform();

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
