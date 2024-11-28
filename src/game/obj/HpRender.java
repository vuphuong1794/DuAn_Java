package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class HpRender {
    protected HP hp;

    public HpRender(double hpInput) {
        hp=new HP(hpInput,hpInput);
    }

    protected void hpRender(Graphics2D g2, Shape shape, double x,double y) {
            double hpx = shape.getBounds().getX() - x ;
            double hpY = shape.getBounds().getY() - y - 10;

            g2.setColor(new Color(70, 70, 70));
            g2.fill(new Rectangle2D.Double(hpx, hpY, Player.PLAYER_SIZE, 2));
            g2.setColor(new Color(253, 91, 91));
            double hpSize = hp.getCurrentHp() / hp.getMAX_HP() * Player.PLAYER_SIZE;
            g2.fill(new Rectangle2D.Double(hpx, hpY, hpSize, 2));
    }


    public boolean updateHP(double cutHP) {
        if (cutHP>=hp.getMAX_HP()) {
            hp.setCurrentHp(0);
        }
        else {
            hp.setCurrentHp(hp.getCurrentHp() - cutHP);
        }
        return hp.getCurrentHp() > 0;
    }

    public void restoreHP(double HP) {
        if ( hp.getCurrentHp()+HP > hp.getMAX_HP()){
            hp.setCurrentHp(hp.getMAX_HP());
        }
        else {
            hp.setCurrentHp(hp.getCurrentHp() + HP);
        }
    }

    public double getHP() {
        return hp.getCurrentHp();
    }

    public void resetHP() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

//    public void draw(Graphics2D g2) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'draw'");
//    }
}