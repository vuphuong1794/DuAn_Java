package game.component;

import game.obj.Bullet;
import game.obj.Player;
import game.obj.Enemy;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
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
    private Image imagemap;
    private float shotTime;

    // Game FPS
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;

    // Game Object
    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> bullets;

    // Khởi động game
    public void start() {
        width = getWidth(); // Lấy chiều rộng của panel
        height = getHeight(); // Lấy chiều cao của panel
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();

        // Cấu hình render cho đồ họa mượt mà hơn
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Khởi động luồng chính của game
        thread = new Thread(() -> {
            while (start) {
                long startTime = System.nanoTime();
                drawBackground();
                drawGame();
                render();
                long time = System.nanoTime() - startTime;

                // Điều chỉnh tốc độ game để duy trì FPS ổn định
                if (time < TARGET_TIME) {
                    long sleep = (TARGET_TIME - time) / 1000000;
                    sleep(sleep);
                }
            }
        });

        initObjectGame();
        initKeyboard();
        initBullets();
        thread.start();
    }

    // Thêm kẻ thù vào game
    private void addEnemy() {
        Random ran = new Random();
        int margin = 50; // Khoảng cách từ mép màn hình
        int enemySize = 300; // Giả sử kích thước của kẻ thù

        // Tạo kẻ thù bên trái màn hình
        int locationY1 = ran.nextInt(height - enemySize - 2 * margin) + margin;
        Enemy enemyLeft = new Enemy();
        enemyLeft.changeLocation(-enemySize, locationY1);
        enemyLeft.changeAngle(0); // Di chuyển sang phải
        enemies.add(enemyLeft);

        // Tạo kẻ thù bên phải màn hình
        int locationY2 = ran.nextInt(height - enemySize - 2 * margin) + margin;
        Enemy enemyRight = new Enemy();
        enemyRight.changeLocation(width + enemySize, locationY2);
        enemyRight.changeAngle(180); // Di chuyển sang trái
        enemies.add(enemyRight);

        // Tạo kẻ thù ở trên màn hình
        int locationX3 = ran.nextInt(width - enemySize - 2 * margin) + margin;
        Enemy enemyTop = new Enemy();
        enemyTop.changeLocation(locationX3, -enemySize);
        enemyTop.changeAngle(90); // Di chuyển xuống
        enemies.add(enemyTop);

        // Tạo kẻ thù ở dưới màn hình
        int locationX4 = ran.nextInt(width - enemySize - 2 * margin) + margin;
        Enemy enemyBottom = new Enemy();
        enemyBottom.changeLocation(locationX4, height + enemySize);
        enemyBottom.changeAngle(270); // Di chuyển lên
        enemies.add(enemyBottom);
    }

    // Khởi tạo các đối tượng trong game
    private void initObjectGame() {
        player = new Player();
        player.changeLocation(150, 150);
        enemies = new ArrayList<>();

        // Tạo luồng riêng để sinh kẻ thù định kỳ
        new Thread(() -> {
            while (start) {
                addEnemy();
                sleep(3000); // Mỗi 3 giây thêm kẻ thù mới
            }
        }).start();
    }

    // Khởi tạo xử lý bàn phím
    private void initKeyboard() {
        key = new Key();
        requestFocus(); // Đảm bảo JComponent có focus để nhận sự kiện bàn phím
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A -> key.setKey_left(true);
                    case KeyEvent.VK_D -> key.setKey_right(true);
                    case KeyEvent.VK_SPACE -> key.setKey_space(true);
                    case KeyEvent.VK_J -> key.setKey_j(true);
                    case KeyEvent.VK_K -> key.setKey_k(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A -> key.setKey_left(false);
                    case KeyEvent.VK_D -> key.setKey_right(false);
                    case KeyEvent.VK_SPACE -> key.setKey_space(false);
                    case KeyEvent.VK_J -> key.setKey_j(false);
                    case KeyEvent.VK_K -> key.setKey_k(false);
                }
            }
        });

        // Khởi động luồng xử lý di chuyển của player và enemies
        new Thread(() -> {
            float rotationSpeed = 0.5f; // Tốc độ thay đổi góc của Player
            while (start) {
                float angle = player.getAngle();
                if (key.isKey_left()) {
                    angle -= rotationSpeed; // Xoay trái
                }
                if (key.isKey_right()) {
                    angle += rotationSpeed; // Xoay phải
                }
                if (key.isKey_space()) {
                    player.speedUp(); // Tăng tốc
                }
                if (key.isKey_j()|| key.isKey_k()){
                    if (shotTime==0){
                        if (key.isKey_j()) {
                            bullets.add(0,new Bullet(player.getX(), player.getY(), player.getAngle(), 5,3f));
                        }
                        else{
                            bullets.add(0,new Bullet(player.getX(), player.getY(), player.getAngle(), 20,3f));
                        }
                    }
                    shotTime++;
                    if (shotTime==15){
                        shotTime=0;
                    }
                }

                else {
                    player.speedDown(); // Giảm tốc
                }
                player.update();
                player.changeAngle(angle);

                // Cập nhật vị trí của tất cả các kẻ thù
                List<Enemy> currentEnemies;
                synchronized (enemies) {
                    currentEnemies = new ArrayList<>(enemies); // Tạo một bản sao an toàn
                }

                for (Enemy enemy : currentEnemies) {
                    if (enemy != null) {
                        enemy.update();
                    }
                }
                sleep(5); // Ngủ 5ms để giảm tải cho CPU
            }
        }).start();
    }
    // Vẽ nền game
    private void drawBackground() {
        // Tải hình ảnh bản đồ nếu chưa được tải
        if (imagemap == null) {
            imagemap = loadImage("/game/image/map.png");
        }

        // Vẽ hình ảnh bản đồ lên nền
        if (imagemap != null) {
            g2.drawImage(imagemap, 0, 0, width, height, null); // Vẽ hình ảnh với kích thước của panel
        } else {
            g2.setColor(new Color(30, 30, 30)); // Màu nền xám đậm nếu không tải được hình ảnh
            g2.fillRect(0, 0, width, height); // Vẽ hình chữ nhật với kích thước của panel
        }
    }

    // Phương thức tải hình ảnh và xử lý lỗi nếu có
    private Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không tải được map");
            return null; // Trả về null nếu xảy ra lỗi khi tải hình ảnh
        }
    }

    private void initBullets() {
        bullets = new ArrayList<>();
        new Thread(() -> {
            while (start) {
                // Tạo một bản sao của danh sách đạn để duyệt qua
                List<Bullet> bulletsCopy;
                synchronized (bullets) {
                    bulletsCopy = new ArrayList<>(bullets);
                }

                for (int i = 0; i < bulletsCopy.size(); i++) {
                    Bullet bullet = bulletsCopy.get(i);
                    if (bullet != null) {
                        bullet.update(); // Cập nhật vị trí của viên đạn
                        // Kiểm tra nếu viên đạn còn trong khung hình
                        if (!bullet.check(width, height)) {
                            // Nếu viên đạn ra ngoài khung hình, thêm vào danh sách xóa
                            synchronized (bullets) {
                                bullets.remove(bullet); // Xóa viên đạn khỏi danh sách gốc
                            }
                        }
                    }
                }
                sleep(1); // Ngủ 1ms để giảm tải cho CPU
            }
        }).start(); // Bắt đầu luồng xử lý đạn
    }



    // Vẽ các đối tượng trong game
    private void drawGame() {
        player.draw(g2); // Vẽ player
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy != null) {
                enemy.draw(g2); // Vẽ từng kẻ thù
            }
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet != null) {
                bullet.draw(g2);
            }
        }
    }

    // Render hình ảnh đã vẽ lên màn hình
    private void render() {
        Graphics g = getGraphics();
        if (g != null) {
            g.drawImage(image, 0, 0, null); // Vẽ hình ảnh từ bộ nhớ đệm lên màn hình
            g.dispose();
        }
    }

    // Dừng lại một khoảng thời gian (ms)
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}
