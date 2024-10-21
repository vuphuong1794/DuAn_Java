package game.component;

import game.obj.Enermy;
import game.obj.Player;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PanelGame extends JComponent {

    private Graphics2D g2;
    private BufferedImage image;
    private int width, height;
    private Thread thread;
    private boolean start = true;
    private Key key;

    //Game FPS
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;

    //Game Object
    private Player player;
    private List<Enermy> enermies;

    public void start(){
        width = getWidth();
        height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(start){
                    long startTime = System.nanoTime();
                    drawBackground();
                    drawGame();
                    render();
                    long time = System.nanoTime() - startTime;
                    if(time < TARGET_TIME){
                        long sleep = (TARGET_TIME - time) / 1000000;
                        sleep(sleep);
                    }

                }
            }
        });
        initObjectGame();
        initKeyboard();
        thread.start();
    }

    private void addEnermy(){
        Random ran = new Random();
        int margin = 50; // Khoảng cách từ mép màn hình
        int enemySize = 300; // Giả sử kích thước của kẻ thù là 50x50 pixels

        // Tạo kẻ thù bên trái màn hình
        int locationY1 = ran.nextInt(height - enemySize - 2*margin) + margin;
        Enermy enemyLeft = new Enermy();
        enemyLeft.changeLocation(-enemySize, locationY1); // Bắt đầu từ ngoài màn hình bên trái
        enemyLeft.changeAngle(0); // Di chuyển sang phải
        enermies.add(enemyLeft);

        // Tạo kẻ thù bên phải màn hình
        int locationY2 = ran.nextInt(height - enemySize - 2*margin) + margin;
        Enermy enemyRight = new Enermy();
        enemyRight.changeLocation(width + enemySize, locationY2); // Bắt đầu từ ngoài màn hình bên phải
        enemyRight.changeAngle(180); // Di chuyển sang trái
        enermies.add(enemyRight);

        // Tạo kẻ thù ở trên màn hình (tùy chọn)
        int locationX3 = ran.nextInt(width - enemySize - 2*margin) + margin;
        Enermy enemyTop = new Enermy();
        enemyTop.changeLocation(locationX3, -enemySize); // Bắt đầu từ trên màn hình
        enemyTop.changeAngle(90); // Di chuyển xuống
        enermies.add(enemyTop);

        // Tạo kẻ thù ở dưới màn hình (tùy chọn)
        int locationX4 = ran.nextInt(width - enemySize - 2*margin) + margin;
        Enermy enemyBottom = new Enermy();
        enemyBottom.changeLocation(locationX4, height + enemySize); // Bắt đầu từ dưới màn hình
        enemyBottom.changeAngle(270); // Di chuyển lên
        enermies.add(enemyBottom);
    }

    //khởi tạo đối tượng
    private void initObjectGame(){
        player = new Player();
        player.changeLocation(150, 150);
        enermies = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(start){
                    addEnermy();
                    sleep(3000);
                }
            }
        }).start();
    }

    private void initKeyboard(){
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_A){
                    key.setKey_left(true);
                } else if(e.getKeyCode() == KeyEvent.VK_D){
                    key.setKey_right(true);
                } else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(true);
                } else if(e.getKeyCode() == KeyEvent.VK_J){
                    key.setKey_j(true);
                } else if(e.getKeyCode() == KeyEvent.VK_K){
                    key.setKey_k(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_A){
                    key.setKey_left(false);
                } else if(e.getKeyCode() == KeyEvent.VK_D){
                    key.setKey_right(false);
                } else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(false);
                } else if(e.getKeyCode() == KeyEvent.VK_J){
                    key.setKey_j(false);
                } else if(e.getKeyCode() == KeyEvent.VK_K){
                    key.setKey_k(false);
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 0.5f; //tăng góc độ khi người dùng ấn nút di chuyển
                while(start){
                    float angle = player.getAngle();
                    if(key.isKey_left()){
                        angle -= s;
                    }
                    if(key.isKey_right()){
                        angle += s;
                    }
                    if(key.isKey_space()){
                        player.speedUp();
                    }
                    else{
                        player.speedDown();
                    }
                    player.update();
                    player.changeAngle(angle);
                    for(int i = 0; i < enermies.size(); i++){
                        Enermy enermy = enermies.get(i);
                        if(enermy != null){
                            enermy.update();
                        }
                    }
                    sleep(5);
                }
            }
        }).start();
    }

    private void drawBackground(){
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, width, height);
    }

    private void drawGame(){
        player.draw(g2);
        for(int i = 0; i < enermies.size(); i++){
            Enermy enermy = enermies.get(i);
            if(enermy != null){
                enermy.draw(g2);
            }
        }
    }

    private void render(){
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    private void sleep(long speed){
        try{
            Thread.sleep(speed);
        } catch(InterruptedException ex){
            System.err.println(ex);
        }
    }
}
