package game.obj;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Bullet {
    private double x;
    private double y;
    private final Shape shape= new Ellipse2D.Double(0,0,50,50);;
    private final Color color=new Color(255,255,255);
    private final float angle;
    private double size=3;
    private float speed=3f;
    public Bullet (double x, double y, float angle) {
        this.x=x;
        this.y=y;
        this.angle=angle;
    }

    public void update(){
        x+=Math.cos(Math.toRadians(angle))*speed;
        y+=Math.sin(Math.toRadians(angle))*speed;

    }
    public boolean check(int width, int height){
        if(x<=-size || x>width || y<=-size || y>height){
            return false;
        }
        else {
            return true;
        }
    }
    public void draw(Graphics2D g2){
        AffineTransform oldTransform = g2.getTransform();
        g2.setColor(color);
        g2.translate(x,y);
        g2.fill(shape);
        g2.setTransform(oldTransform);
    }

    public Shape getShape(){
        return new Area(new Ellipse2D.Double(x,y,size,size));
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getSize() {
        return size;
    }
    public double getCenterX() {
        return x+size/2;
    }
    public double getCenterY() {
        return y+size/2;
    }

}
