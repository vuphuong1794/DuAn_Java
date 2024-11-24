package game.component;

import game.obj.Bullet;
import game.obj.Effect;
import game.obj.Player;
import game.obj.Enemy;
import game.obj.item.Item;
import game.obj.sound.sound;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
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
    private List<Effect> boomEffects;
    private List<Item> items;
    private sound Sound;
    private Point mousePosition; // Mouse position

    private int score = 0;
    private int ammoCount = 0; // Số lượng đạn hiện có
    private long lastShotTime = 0; // Thời điểm bắn viên đạn cuối cùng
    // Lưu thời gian tạo hiệu ứng nổ
    private long lastExplosionTime = 0;
    private boolean hasAmmo = false; // Kiểm tra có đạn hay không
    private static final int MAX_ITEMS = 5; // Số lượng item tối đa trên bản đồ

    public PanelGame() {
        // Tạo trình để lắng nghe chuyển động, theo giỏ vị trí chuột
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                mousePosition = e.getPoint();
            }
        });
    }

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

                // Tính góc quay Player - chuột
                if (mousePosition != null) {
                    double dx = mousePosition.x - player.getX();
                    double dy = mousePosition.y - player.getY();

                    // Tính toán góc của Player và chuột
                    double angleToMouse = Math.toDegrees(Math.atan2(dy, dx));

                    // Góc quay được 360 độ
                    if (angleToMouse < 0) {
                        angleToMouse += 360;
                    }

                    // Cập nhật góc xoay của Player
                    player.changeAngle((float) angleToMouse);
                }

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
        Sound.soundZombie();
        if (enemies.size() >= 30) {
            return; // If there are already 20 enemies, do not add more
        }
        Random ran = new Random();
        int margin = 50;
        int enemySize = 100;

        int locationY1 = ran.nextInt(height - enemySize - 2 * margin) + margin;
        Enemy enemyLeft = new Enemy();
        enemyLeft.changeLocation(-enemySize + 5, locationY1); // đứng gần cạnh viền
        enemyLeft.changeAngle(0);
        enemies.add(enemyLeft);

        int locationY2 = ran.nextInt(height - enemySize - 2 * margin) + margin;
        Enemy enemyRight = new Enemy();
        enemyRight.changeLocation(width - 10, locationY2);
        enemyRight.changeAngle(180); // Moving left
        enemies.add(enemyRight);

        int locationX3 = ran.nextInt(width - enemySize - 2 * margin) + margin;
        Enemy enemyTop = new Enemy();
        enemyTop.changeLocation(locationX3, -enemySize + 10);
        enemyTop.changeAngle(90); // Moving down
        enemies.add(enemyTop);

        int locationX4 = ran.nextInt(width - enemySize - 2 * margin) + margin;
        Enemy enemyBottom = new Enemy();
        enemyBottom.changeLocation(locationX4, height - 10);
        enemyBottom.changeAngle(270); // Moving up
        enemies.add(enemyBottom);
    }

    // Khởi tạo các đối tượng trong game
    private void initObjectGame() {
        Sound = new sound();
        player = new Player();
        items = new ArrayList<>();
        player.changeLocation(150, 150);
        enemies = new ArrayList<>();
        boomEffects = new ArrayList<>();
        // Tạo luồng riêng để sinh kẻ thù định kỳ
        new Thread(() -> {
            while (start) {
                addEnemy();
                sleep(3000); // Mỗi 3 giây thêm kẻ thù mới

                if (items.size() < MAX_ITEMS) {
                    addRandomItem();
                }
                sleep(5000); // 5 giây tạo 1 item mới nếu chưa đạt max
            }
        }).start();
    }

    // Phương thức tạo item ngẫu nhiên
    private void addRandomItem() {
        Random rand = new Random();
        int margin = 50;

        // Tạo vị trí ngẫu nhiên trong vùng an toàn
        double x = margin + rand.nextDouble() * (width - 2 * margin - Item.ITEM_SIZE);
        double y = margin + rand.nextDouble() * (height - 2 * margin - Item.ITEM_SIZE);

        items.add(new Item(x, y));
    }


    private void resetGame(){
        score=0;
        items.clear();
        hasAmmo = false;
        enemies.clear();
        bullets.clear();
        player.changeLocation(150, 150);
        player.reset();
        ammoCount = 0;    }

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
                    case KeyEvent.VK_S -> key.setKey_down(true);
                    case KeyEvent.VK_W -> key.setKey_up(true);
                    case KeyEvent.VK_J -> key.setKey_j(true);
                    case KeyEvent.VK_K -> key.setKey_k(true);
                    case KeyEvent.VK_ENTER -> key.setKey_enter(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A -> key.setKey_left(false);
                    case KeyEvent.VK_D -> key.setKey_right(false);
                    case KeyEvent.VK_S -> key.setKey_down(false);
                    case KeyEvent.VK_W -> key.setKey_up(false);
                    case KeyEvent.VK_J -> key.setKey_j(false);
                    case KeyEvent.VK_K -> key.setKey_k(false);
                    case KeyEvent.VK_ENTER -> key.setKey_enter(false);

                }
            }
        });

        // Khởi động luồng xử lý di chuyển của player và enemies
        new Thread(() -> {
            while (start) {
                if(player.isAlive()) {
                    float speed = 1f; // Tốc độ di chuyển của Player
                    if (key.isKey_left()) {
                        player.changeLocation(player.getX() - speed, player.getY()); // Di chuyển sang trái
                    }
                    if (key.isKey_right()) {
                        player.changeLocation(player.getX() + speed, player.getY()); // Di chuyển sang phải
                    }
                    if (key.isKey_up()) {
                        player.changeLocation(player.getX(), player.getY() - speed); // Di chuyển lên
                    }
                    if (key.isKey_down()) {
                        player.changeLocation(player.getX(), player.getY() + speed); // Di chuyển xuống
                    }
                    if (key.isKey_j() || key.isKey_k()) {
                        long currentTime = System.currentTimeMillis();
                        if (shotTime == 0) {
                            if (key.isKey_j()) {
                                bullets.add(0, new Bullet(player.getX(), player.getY(), player.getAngle(), 5, 3f));
                                Sound.soundShoot();
                            }
                            if (key.isKey_k()) {
                                if (ammoCount > 0) { // Chỉ bắn được khi có đạn
                                    // Kiểm tra đã qua 1 giây kể từ lần bắn trước
                                    if (currentTime - lastShotTime >= 1000) { // 1000ms = 1 giây
                                        bullets.add(0, new Bullet(player.getX(), player.getY(), player.getAngle(), 20, 3f));
                                        Sound.soundShotgun();
                                        ammoCount--; // Giảm số đạn sau khi bắn
                                        hasAmmo = ammoCount > 0; // Cập nhật trạng thái đạn
                                        lastShotTime = currentTime; // Cập nhật thời điểm bắn mới nhất
                                    }
                                }
                            }

                        }
                        shotTime++;
                        if (shotTime == 15) {
                            shotTime = 0;
                        }
                    }
                    player.update();
                    checkItems();
                }
                else{
                    if(key.isKey_enter()) {
                        resetGame();
                    }
                }

                for(int i=0; i<enemies.size(); i++){
                    Enemy enemy = enemies.get(i);
                    if(enemy != null){
                        enemy.updateMovement(
                                player.getX(),
                                player.getY(),
                                player.getAngle(),
                                player.getSpeed()  // Giả sử bạn có getter cho speed của player
                        );
                        if(!enemy.check(width, height)){
                            enemies.remove(i);
                            System.out.println("removed");
                        }else{
                            //cham vao thi zombie se ....
                            if(player.isAlive()) {
                                checkPlayer(enemy);
                            }
                        }
                    }
                }

                sleep(5); // Ngủ 5ms để giảm tải cho CPU
            }
        }).start();
    }

    private void checkBullets(Bullet bullet){
        // Duyệt qua danh sách zombie
        for(int i=0; i<enemies.size(); i++){
            Enemy enemy = enemies.get(i);
            if(enemy!=null){
                // Tạo một vùng (Area) dựa trên hình dạng của viên đạn
                Area area = new Area(bullet.getShape());
                // Tính toán giao cắt giữa hình dạng của viên đạn và kẻ thù
                area.intersect(enemy.getShape());
                // Nếu vùng giao nhau không rỗng, tức là viên đạn đã va chạm với zombie
                if(!area.isEmpty()){

                    boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(),3, 5, 60, 0.5f, new Color(230, 207, 105)));
                    // Cập nhật HP của kẻ thù dựa trên kích thước viên đạn, nếu HP = 0
                    if(!enemy.updateHP(bullet.getSize())) {
                        score++;
                        enemies.remove(enemy);
                        //Sound.soundZombie();
                        double x = enemy.getX() + Enemy.ENEMY_SIZE / 2;
                        double y = enemy.getY() + Enemy.ENEMY_SIZE / 2;
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));

                    }

                    bullets.remove(bullet);
                }
            }
        }
    }

    private void checkPlayer(Enemy enemy) {
        if (enemy != null) {
            // Tạo vùng giao nhau giữa hình dạng của player và enemy
            Area area = new Area(player.getShape());
            area.intersect(enemy.getShape());

            // Kiểm tra nếu có va chạm (vùng giao nhau không rỗng)
            if (!area.isEmpty()) {
                // Lấy máu của enemy
                double enemyHp = enemy.getHP();

                // Lấy thời gian hiện tại
                long currentTime = System.currentTimeMillis();

                // Chỉ xử lý va chạm nếu đã qua 3 giây kể từ lần va chạm trước
                if (currentTime - lastExplosionTime >= 3000) {
                    // Kiểm tra nếu enemy chết khi va chạm với player
                    if (!enemy.updateHP(player.getHP())) {
                        enemies.remove(enemy);
                        Sound.soundZombie();

                        // Tính toán vị trí trung tâm của enemy để tạo hiệu ứng nổ
                        double x = enemy.getX() + Enemy.ENEMY_SIZE / 2;
                        double y = enemy.getY() + Enemy.ENEMY_SIZE / 2;

                        // Tạo nhiều hiệu ứng nổ với các màu và kích thước khác nhau
                        boomEffects.add(new Effect(x, y,5, 5, 75, 0.05f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y,5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y,10, 10, 100, 0.3f, new Color(230, 207, 105)));
                        boomEffects.add(new Effect(x, y,10, 5, 100, 0.5f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y,10, 5, 150, 0.2f, new Color(255, 255, 255)));

                        lastExplosionTime = currentTime;
                    }

                    // Kiểm tra nếu player chết khi va chạm với enemy
                    if (!player.updateHP(enemyHp)) {
                        player.setAlive(false);
                        Sound.soundZombie();

                        // Tính toán vị trí trung tâm của player để tạo hiệu ứng nổ
                        double x = player.getX() + Player.PLAYER_SIZE / 2;
                        double y = player.getY() + Player.PLAYER_SIZE / 2;

                        // Tạo nhiều hiệu ứng nổ cho player giống như enemy
                        boomEffects.add(new Effect(x, y,5, 5, 75, 0.05f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y,5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y,10, 10, 100, 0.3f, new Color(230, 207, 105)));
                        boomEffects.add(new Effect(x, y,10, 5, 100, 0.5f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y,10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    }
                    else{
                        Sound.soundHit();
                    }
                }
            }
        }
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
                        checkBullets(bullet); //kiểm tra nếu viên đạn bắn trúng zombie
                        // Kiểm tra nếu viên đạn còn trong khung hình
                        if (!bullet.check(width, height)) {
                            // Nếu viên đạn ra ngoài khung hình, thêm vào danh sách xóa
                            synchronized (bullets) {
                                bullets.remove(bullet); // Xóa viên đạn khỏi danh sách gốc
                            }
                        }
                    }
                }
                for(int i = 0; i<boomEffects.size(); i++){
                    Effect boomEffect = boomEffects.get(i);
                    if (boomEffect != null) {
                        boomEffect.update();
                        if(!boomEffect.check()){
                            boomEffects.remove(boomEffect);
                        }
                    }else{
                        boomEffects.remove(boomEffect);
                    }
                }
                sleep(1); // Ngủ 1ms để giảm tải cho CPU
            }
        }).start(); // Bắt đầu luồng xử lý đạn
    }

    // Vẽ các đối tượng trong game
    private void drawGame() {
        if (player.isAlive()) {
            player.draw(g2); // Vẽ player
        }

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
        for(int i=0; i<boomEffects.size(); i++){
            Effect boomEffect = boomEffects.get(i);
            if (boomEffect != null) {
                boomEffect.draw(g2);
            }
        }

        for (Item item : items) {
            if (!item.isCollected()) {
                item.draw(g2);
            }
        }

        //hiển thị trạng thái
        g2.setColor(Color.WHITE);
        g2.drawString("Score: "+score, 30, 40);
        g2.drawString("Ammo: " + ammoCount, 30, 60);

        if(!player.isAlive()){
            String text = "GAME OVER";
            String textKey = "Press enter to continue";
            g2.setFont(getFont().deriveFont(Font.BOLD, 50f));
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r2 = fm.getStringBounds(text, g2);
            double textWidth = r2.getWidth();
            double textHeight = r2.getHeight();
            double x = (width - textWidth) / 2;
            double y = (height - textHeight) / 2;
            g2.drawString(text, (int)x, (int)y + fm.getAscent());
            g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(textKey, g2);
            textWidth = r2.getWidth();
            x = (width - textWidth) / 2;
            y = (height - textHeight) / 2;
            g2.drawString(textKey, (int)x, (int)y + fm.getAscent() + 50);
        }
    }

    //kiểm tra chạm item
    private void checkItems() {
        Iterator<Item> iterator = items.iterator();
        // Lặp qua tất cả các items trên bản đồ
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (!item.isCollected()) {
                // Tạo vùng va chạm bằng cách lấy hình dạng của player
                Area area = new Area(player.getShape());
                // Tính phần giao của hình dạng player và item
                area.intersect(new Area(item.getShape()));
                // Nếu có va chạm (phần giao không rỗng)
                if (!area.isEmpty()) {
                    // Đánh dấu item đã được thu thập
                    item.collect();
                    ammoCount+=10;
                    // Cập nhật trạng thái đạn của player
                    hasAmmo = true;
                    //Sound.soundCollectItem(); // Thêm âm thanh nhặt item nếu có
                    // Xóa item khỏi danh sách
                    iterator.remove();
                }
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