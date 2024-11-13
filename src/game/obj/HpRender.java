package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class HpRender {
    private final HP hp;

    public HpRender(HP hp) {
        this.hp = hp;
    }

    protected void hpRender(Graphics2D g2, Shape shape, double y) {
        if(hp.getCurrentHp() != hp.getMAX_HP()) {
            // Lấy tọa độ của shape
            Rectangle2D bounds = shape.getBounds2D();

            // Tính toán vị trí x để thanh máu nằm giữa nhân vật
            double hpX = bounds.getCenterX() - (Player.PLAYER_SIZE / 2);
            // Tính toán vị trí y để thanh máu nằm trên đầu nhân vật
            double hpY = bounds.getY() - 10;  // -10 là khoảng cách từ đầu nhân vật đến thanh máu

            // Vẽ phần nền của thanh máu (màu xám)
            g2.setColor(new Color(70, 70, 70));
            g2.fill(new Rectangle2D.Double(hpX, hpY, Player.PLAYER_SIZE, 2));

            // Vẽ phần máu hiện tại (màu đỏ)
            g2.setColor(new Color(253, 91, 91));
            double hpSize = hp.getCurrentHp() / hp.getMAX_HP() * Player.PLAYER_SIZE;
            g2.fill(new Rectangle2D.Double(hpX, hpY, hpSize, 2));
        }
    }

    public boolean updateHP(double cutHP) {
        hp.setCurrentHp(hp.getCurrentHp() - cutHP);
        return hp.getCurrentHp() > 0;
    }

    public double getHP() {
        return hp.getCurrentHp();
    }

    public void resetHP() {
        hp.setCurrentHp(hp.getMAX_HP());
    }
}