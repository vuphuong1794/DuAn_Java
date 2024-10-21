package game.obj;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Player {

    public Player(){
        this.image = new ImageIcon(getClass().getResource("/game/image/Player.png")).getImage();
    }

    public static final double PLAYER_SIZE = 84;
    private double x, y; //vị trí x, y nhân vật

    // NHÂN VẬT
    private float speed = 0f;
    private final float MAX_SPEED = 1f;

    private float angle = 0f;
    private final Image image; //ảnh nhân vật
    private boolean speedUp;

    //thay đổi vị trí
    public void changeLocation(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void update(){
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
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
        g2.translate(x, y); //di chuyển vị trí
        AffineTransform tran = g2.getTransform();
        tran.rotate(Math.toRadians(angle), PLAYER_SIZE / 2, PLAYER_SIZE / 2); //xoay nhân vật
        g2.drawImage(image, tran,null);

        g2.setTransform(oldTransform); //lưu trữ thay đổi
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

    public void speedUp(){
        speedUp = true;
        if(speed > MAX_SPEED){
            speed = MAX_SPEED;
        } else{
            speed += 0.01f;
        }
    }

    public void speedDown(){
        speedUp = false;
        if(speed <= 0){
            speed = 0f;
        }else{
            speed -= 0.01f;
        }
    }
}
