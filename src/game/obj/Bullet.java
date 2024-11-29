package game.obj;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Bullet {
    private double x;
    private double y;
    private final Shape shape= new Ellipse2D.Double(0,0,10,10);;
    private final Color color=new Color(255,255,255);
    private final float angle;
    private double damage=13f;
    private float speed=3f;
    private float offsetX;
    private float offsetY;
    public int type;
    public Image image;
    public Bullet (double x, double y, float angle, float offsetX, float offsetY, String nameGun) {
        image=loadImage("/game/image/bullet.png");
        this.x=x;
        this.y=y;
        this.angle=angle;
        this.offsetX=offsetX;
        this.offsetY=offsetY;
        if(nameGun.equals("pistol")){
            damage=6f;
            type=1;
            speed=3f;
        }
        else if (nameGun.equals("rifle")){
            damage=4f;
            type=2;
            speed=5f;
        }
        else if (nameGun.equals("sniper")){
            damage=20f;
            type=3;
            speed=8f;
        }
        else if (nameGun.equals("grenade")){
            damage=20f;
            type=4;
            speed=1f;

        }
    }

    public void update(){
        x+=Math.cos(Math.toRadians(angle))*speed;
        y+=Math.sin(Math.toRadians(angle))*speed;

    }
    public boolean check(int width, int height){
        if(x<=0 || x>width || y<=0|| y>height){
            return false;
        }
        else {
            return true;
        }
    }
    public void draw(Graphics2D g2){
        AffineTransform oldTransform = g2.getTransform();
       // g2.setColor(color);
        g2.translate(x,y);
        g2.rotate(Math.toRadians(angle)); // Rotate gun based on player's angle
        g2.translate(offsetX, offsetY);
        g2.scale(0.1f,0.1f);
        //g2.fill(shape);
        g2.drawImage(image,20,20,null);

        g2.setTransform(oldTransform);
    }

    public Shape getShape(){
        return new Area(new Ellipse2D.Double(x,y,damage,damage));
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getDamage() {
        return damage;
    }
    public double getCenterX() {
        return x+damage/2;
    }
    public double getCenterY() {
        return y+damage/2;
    }

    public Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không tải được anhr");
            return null; // Trả về null nếu xảy ra lỗi khi tải hình ảnh
        }
    }
}
