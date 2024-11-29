package game.component;


import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import java.awt.Robot;

import game.obj.*;
import game.obj.item.Item;
import game.obj.sound.sound;
import game.obj.Map;
import game.obj.Gun;

import game.main.Main;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import javax.swing.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


import net.java.games.input.Component;
public class PanelGame extends JComponent {

    private Controller ps5Controller;

    private List<Explosion> explosions = new ArrayList<>();
    private Graphics2D g2;
    private BufferedImage image;
    private int width, height;
    private Thread thread;
    private boolean start = true;
    private Key key;
    private Image imagemap;
    private long shotTime;

    // Game FPS
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;

    // Game Object
    private Map map;
    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<Effect> boomEffects;
    private List<Item> items;
    private List <Gun> GunList;
    private Gun gunEquip;

    private sound Sound;
    private Point mousePosition; // Mouse position
    private Main main;

    //minimap
    private static final int MINIMAP_SIZE = 100;  // Size of the minimap
    private static final int MINIMAP_MARGIN = 10;  // Margin from the edges
    private BufferedImage minimapBuffer;  // Buffer for the minimap

    private int score = 0;
    private int ammoCount = 0; // Số lượng đạn hiện có
    private long lastShotTime = 0; // Thời điểm bắn viên đạn cuối cùng
    private long startTime;  // Thời gian bắt đầu
    private long endTime;    // Thời gian kết thúc
    private String playerName;
    private Long finalElapsedTime = null; // Lưu thời gian cuối cùng khi game kết thúc

    // Lưu thời gian tạo hiệu ứng nổ
    private long lastExplosionTime = 0;
    private static final int MAX_ITEMS = 5; // Số lượng item tối đa trên bản đồ

    private boolean isController=false;

    public PanelGame(Player player, Main main) {
        // Tạo trình để lắng nghe chuyển động, theo giỏ vị trí chuột
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                mousePosition = e.getPoint();
            }
        });
        isController=false;
        this.player = player;
        this.main = main;
    }

    // Khởi động game
    public void start(Player player) {
        playerName = player.getPlayerName();
        finalElapsedTime = null; // Reset thời gian chơi khi bắt đầu lại game

        width = getWidth(); // Lấy chiều rộng của panel
        height = getHeight(); // Lấy chiều cao của panel
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();

        //minimap_display
        minimapBuffer = new BufferedImage(MINIMAP_SIZE, MINIMAP_SIZE, BufferedImage.TYPE_INT_ARGB);

        // Cấu hình render cho đồ họa mượt mà hơn
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        final long[] lastTime = {System.currentTimeMillis()};

        // Khởi động luồng chính của game
        thread = new Thread(() -> {
            while (start) {
                long currentTime = System.currentTimeMillis();
                int deltaTime = (int) (currentTime - lastTime[0]); // Time elapsed since last frame
                lastTime[0] = currentTime;
                updateExplosions(deltaTime);
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
        try{initGamepad();}
        catch(Exception e){e.printStackTrace();}

        initBullets();
        thread.start();
    }

    private void initGamepad() throws AWTException {
        Robot robot = new Robot();
        // Detect PS5 controller
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();


        for (Controller controller : controllers) {
            System.out.println("Controller Name: " + controller.getName());
            System.out.println("Controller Type: " + controller.getType());
            // Kiểm tra tên của controller
            if (controller.getName().contains("DualSense") || controller.getName().contains("Controller")) {
                ps5Controller = controller;
                isController = true;
                break;
            }
        }

        if (isController) {
            System.out.println("PS5 Controller detected successfully!");

            // Add a gamepad input thread
            new Thread(() -> {
                while (start) {
                    if (ps5Controller.poll()) {
//                        for (Component component : ps5Controller.getComponents()) {
//                            System.out.println("Component: " + component.getName() + " | Identifier: " + component.getIdentifier());
//                        }
                        // Movement using left analog stick
                        Component leftStickX = ps5Controller.getComponent(Component.Identifier.Axis.X);
                        Component leftStickY = ps5Controller.getComponent(Component.Identifier.Axis.Y);

                        float stickXValue = leftStickX.getPollData();
                        float stickYValue = leftStickY.getPollData();

                        // Right stick configuration
                        Component rightStickX = ps5Controller.getComponent(Component.Identifier.Axis.RX);
                        Component rightStickY = ps5Controller.getComponent(Component.Identifier.Axis.RY);
                        float rightStickXValue = rightStickX.getPollData();
                        float rightStickYValue = rightStickY.getPollData();
                        //System.out.println("RX Axis: " + rightStickX.getPollData());
                        //System.out.println("RY Axis: " + rightStickY.getPollData());
                        // Deadzone to prevent drift
                        float deadzone = 0.2f;

///////////////////////////////Check if ps5Controller is pressed///////////////////////////////////////////////////////////
                        EventQueue eventQueue = ps5Controller.getEventQueue();
                        Event event = new Event();

                        while (eventQueue.getNextEvent(event)) {
                            String componentName = event.getComponent().getName();
                            float value = event.getValue();

                            // Nếu có input từ tay cầm
                            if (value> deadzone || value < -deadzone) {
                                isController=true;
                                //System.out.println("Gamepad Input Detected: " + componentName + " Value: " + value);
                            }
                        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
                        if (isController) {
                            //RIGHT STICK
                            // Get the current mouse position
                            if (rightStickXValue > deadzone ||
                                    rightStickYValue > deadzone ||
                                    rightStickXValue < -deadzone ||
                                    rightStickYValue < -deadzone)
                            {
                                Point mousePosition = MouseInfo.getPointerInfo().getLocation();

                                // Scale mouse movement
                                float sensitivity = 1.0f; // Adjust for faster/slower movement
                                int moveX = (int) (rightStickXValue * sensitivity);
                                int moveY = (int) (rightStickYValue * sensitivity);

                                // Calculate new mouse position (absolute screen coordinates)
                                int newX = (int) mousePosition.getX() + moveX;
                                int newY = (int) mousePosition.getY() + moveY;
                                //System.out.println("newX: " + newX + " newY: " + newY + " rightStickXvalue: " + rightStickXValue + " rightStickYvalue: " + rightStickYValue);
                                // Move the mouse to the new position
                                //robot.mouseMove(newX, newY);
                            }


                            //LEFT STICK
                            // Horizontal movement
                            if (stickXValue > deadzone) {
                                key.setKey_right(true);
                                key.setKey_left(false);
                            } else if (stickXValue < -deadzone) {
                                key.setKey_left(true);
                                key.setKey_right(false);
                            } else {
                                key.setKey_left(false);
                                key.setKey_right(false);
                            }

                            // Vertical movement
                            if (stickYValue > deadzone) {
                                key.setKey_down(true);
                                key.setKey_up(false);
                            } else if (stickYValue < -deadzone) {
                                key.setKey_up(true);
                                key.setKey_down(false);
                            } else {
                                key.setKey_up(false);
                                key.setKey_down(false);
                            }

                            // Button mappings
                            Component crossButton = ps5Controller.getComponent(Component.Identifier.Button._0);
                            Component circleButton = ps5Controller.getComponent(Component.Identifier.Button._1);
                            Component squareButton = ps5Controller.getComponent(Component.Identifier.Button._2);
                            Component triangleButton = ps5Controller.getComponent(Component.Identifier.Button._3);
                            Component RZAxis = ps5Controller.getComponent(Component.Identifier.Button._7);

                            // Shooting (R1 button)
                            if (RZAxis != null && RZAxis.getPollData() > 0.5f) {
                                key.setMouseLeftClick(true);
                            } else {
                                key.setMouseLeftClick(false);
                            }

                            // Gun selection
                            if (crossButton != null && crossButton.getPollData() > 0.5f) {
                                key.setKey_1(true);
                            }
                            if (circleButton != null && circleButton.getPollData() > 0.5f) {
                                key.setKey_2(true);
                            }
                            if (squareButton != null && squareButton.getPollData() > 0.5f) {
                                key.setKey_3(true);
                            }
                            if (triangleButton != null && triangleButton.getPollData() > 0.5f) {
                                key.setKey_4(true);
                            }

                            // Reset/Enter
                            Component startButton = ps5Controller.getComponent(Component.Identifier.Button._6);
                            if (startButton != null && startButton.getPollData() > 0.5f) {
                                key.setKey_enter(true);
                            } else {
                                key.setKey_enter(false);
                            }
                        }
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            System.out.println("No PS5 controller detected");
        }
    }

    public void displayTop5Players(Graphics2D g2) {
        String url = "jdbc:mysql://localhost:3306/zombiedoomdays";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            String query = "SELECT player_name, score FROM game_scores ORDER BY score DESC LIMIT 5";
            ResultSet resultSet = statement.executeQuery(query);

            // Top 5 title
            g2.setFont(new Font("Arial", Font.BOLD, 35));
            g2.setColor(new Color(173, 216, 230));
            g2.drawString("Top 5 Players", width / 2 + 150, height / 2 - 70);

            // Table content
            g2.setFont(new Font("Arial", Font.PLAIN, 25));
            int yPosition = height / 2 - 30;
            int rank = 1;

            while (resultSet.next() && rank <= 5) {
                String playerInfo = rank + ". " +
                        resultSet.getString("player_name") +
                        " - " +
                        resultSet.getInt("score");

                g2.setColor(rank % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
                g2.drawString(playerInfo, width / 2 + 150, yPosition);

                yPosition += 50;
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void savePlayerScore() {
        String url = "jdbc:mysql://localhost:3306/zombiedoomdays";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO game_scores (player_name, score, play_time) VALUES (?, ?, ?)")) {

            //System.out.println("test loi 1");
            preparedStatement.setString(1, playerName);
            //System.out.println("test loi 2");
            preparedStatement.setInt(2, score);
            //System.out.println("test loi 3");
            preparedStatement.setLong(3, finalElapsedTime != null ? finalElapsedTime : 0);
            //System.out.println("test loi 4");

            preparedStatement.executeUpdate();

            //System.out.println("test loi");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addExplosion(double x, double y, Image image, int duration) {
        explosions.add(new Explosion(x, y, image, duration));
    }
    private void updateExplosions(int deltaTime) {
        Iterator<Explosion> iterator = explosions.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();
            explosion.update(deltaTime);
            if (explosion.isExpired()) {
                iterator.remove();
            }
        }
    }

    private void renderExplosions(Graphics2D g2, Bullet bullet) {
        for (Explosion explosion : explosions) {
            explosion.render(g2,bullet);
        }
    }

    //Minimap
    private void drawMinimap() {
        Graphics2D mg = minimapBuffer.createGraphics();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int widthScreen = (int)screenSize.getWidth();
        int heightScreen = (int)screenSize.getHeight();
        // Clear minimap background
        mg.setColor(new Color(0, 0, 0, 180));
        mg.fillRect(0, 0, MINIMAP_SIZE, MINIMAP_SIZE);

        // Draw border
        mg.setColor(Color.WHITE);
        mg.drawRect(0, 0, MINIMAP_SIZE - 1, MINIMAP_SIZE - 1);

        // Calculate scale factors
        float scaleX = (float) MINIMAP_SIZE / width;
        float scaleY = (float) MINIMAP_SIZE / height;

        // Draw player (as a white dot)
        int playerMinimapX = (int) (player.getX() * scaleX);
        int playerMinimapY = (int) (player.getY() * scaleY);
        mg.setColor(Color.WHITE);
        mg.fillOval(playerMinimapX - 2, playerMinimapY - 2, 4, 4);

        // Draw enemies (as red dots)
        mg.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            int enemyMinimapX = (int) (enemy.getX() * scaleX);
            int enemyMinimapY = (int) (enemy.getY() * scaleY);
            mg.fillOval(enemyMinimapX - 2, enemyMinimapY - 2, 4, 4);
        }

        // Draw items (as yellow dots)
        mg.setColor(Color.YELLOW);
        try {
            for (Item item : items) {
                int itemMinimapX = (int) (item.getX() * scaleX);
                int itemMinimapY = (int) (item.getY() * scaleY);
                mg.fillOval(itemMinimapX - 1, itemMinimapY - 1, 3, 3);
            }
        }
        catch ( Exception exception){
            System.out.println(exception);
        }

        mg.dispose();

        // Draw the minimap on the main graphics context
        // Position it below score and ammo
        int minimapX = widthScreen-200;
        int minimapY = 10; // Adjust this value based on where your score/ammo text ends
        g2.drawImage(minimapBuffer, minimapX, minimapY, null);
    }

    private void drawVolumeControl() {
        // Vẽ nhãn âm lượng
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Âm lượng:", 30, 100);

        // Vẽ thanh trượt tùy chỉnh
        int sliderX = 133;
        int sliderY = 85;
        int sliderWidth = 150;
        int sliderHeight = 20;

        // Vẽ đường nền của thanh trượt
        g2.setColor(Color.GRAY);
        g2.fillRect(sliderX, sliderY, sliderWidth, sliderHeight);

        // Tính toán vị trí của nút trượt dựa trên âm lượng hiện tại
        int knobWidth = 20;
        int knobX = sliderX + (int)(Sound.getVolume() * (sliderWidth - knobWidth));

        // Vẽ nút trượt
        g2.setColor(Color.WHITE);
        g2.fillRect(knobX, sliderY, knobWidth, sliderHeight);

        // Vẽ viền cho thanh trượt
        g2.setColor(Color.BLACK);
        g2.drawRect(sliderX, sliderY, sliderWidth, sliderHeight);
    }

    // Thêm kẻ thù vào game
    private void addEnemy() {
        Sound.soundZombie();
        if (enemies.size() >= 20) {
            return; // If there are already 20 enemies, do not add more
        }
        Random ran = new Random();
        int margin = 50;
        int enemySize = 100;
        int spawnDirection = ran.nextInt(4) + 1; // Random number between 1 and 4
        Enemy enemy = new Enemy();

        switch (spawnDirection) {
            case 1: // Spawn enemy on the left
                int locationY1 = ran.nextInt(height - enemySize - 2 * margin) + margin;
                enemy.changeLocation(-enemySize + 5, locationY1); // Near the left edge
                enemy.changeAngle(0); // Moving right
                break;

            case 2: // Spawn enemy on the right
                int locationY2 = ran.nextInt(height - enemySize - 2 * margin) + margin;
                enemy.changeLocation(width - 10, locationY2); // Near the right edge
                enemy.changeAngle(180); // Moving left
                break;

            case 3: // Spawn enemy on the top
                int locationX3 = ran.nextInt(width - enemySize - 2 * margin) + margin;
                enemy.changeLocation(locationX3, -enemySize + 10); // Near the top edge
                enemy.changeAngle(90); // Moving down
                break;

            case 4: // Spawn enemy on the bottom
                int locationX4 = ran.nextInt(width - enemySize - 2 * margin) + margin;
                enemy.changeLocation(locationX4, height - 10); // Near the bottom edge
                enemy.changeAngle(270); // Moving up
                break;
        }

// Add the new enemy to the list
        enemies.add(enemy);

    }

    // Khởi tạo các đối tượng trong game
    private void initObjectGame() {
        Sound = new sound();
        player = new Player();
        player.setPlayerName(playerName);
        items = new ArrayList<>();
        player.changeLocation(300, 300);
        enemies = new ArrayList<>();
        boomEffects = new ArrayList<>();
        GunList = new ArrayList<>();
        map=new Map();
        // Create and add different gun types to GunList
        Gun pistol = new Gun("pistol", 100, 500, player.getX(), player.getY());
        Gun rifle = new Gun("rifle", 0, 300, player.getX(), player.getY());
        Gun sniper = new Gun("sniper", 0, 1000, player.getX(), player.getY());
        Gun grenade = new Gun("grenade", 0, 2000, player.getX(), player.getY());

        // Add guns to GunList
        GunList.add(pistol);
        GunList.add(rifle);
        GunList.add(sniper);
        GunList.add(grenade);
        gunEquip=GunList.getFirst();
        // Tạo luồng riêng để sinh kẻ thù định kỳ
        new Thread(() -> {
            while (start) {
                addEnemy();
                sleep(2000); // Mỗi 2 giây thêm kẻ thù mới

                if (items.size() < MAX_ITEMS) {
                    System.out.println("items is created");
                    addRandomItem();
                }
                sleep(1000); // 1 giây tạo 1 item mới nếu chưa đạt max
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

        int randomItem = (int)(Math.random() * 4); // 0 to 100
        System.out.println("items is created at "+x+" "+y+" with type "+randomItem);
        items.add(new Item(x, y, randomItem));
    }

    private void resetGame(){
        score=0;
        items.clear();
        enemies.clear();
        bullets.clear();
        ammoCount = 0;
        startTime = System.nanoTime();
        finalElapsedTime=null;
        player.changeLocation(300, 300);
        player.reset();

        // Reset all guns to initial state
        for (Gun gun : GunList) {
            gun.resetAmmo(); // Add this method to your Gun class to reset ammo
        }

        // Reset to default gun
        gunEquip = GunList.get(0);

        ammoCount = 0;
        startTime = System.nanoTime();
        finalElapsedTime = null;
    }

    private void updateVolume(int mouseX, int sliderX, int sliderWidth) {
        // Tính toán âm lượng dựa trên vị trí chuột
        float volume = Math.max(0, Math.min(1f, (float) (mouseX - sliderX) / sliderWidth));
        System.out.println("number of volumn after change: "+volume);
        Sound.setVolume(volume); // Cập nhật âm lượng vào hệ thống
    }

    private void initKeyboard() {
        key = new Key();
        requestFocus(); // Ensure the JComponent has focus to receive keyboard events

        // Mouse listeners for click and release
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isController=false;
                if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
                    key.setMouseLeftClick(true);
                }


                // Check if the mouse is within the slider bounds using mousePosition
                if (mousePosition.x >= 135 && mousePosition.x <= 280 &&
                        mousePosition.y >= 85 && mousePosition.y <= 105) {
                    updateVolume(mousePosition.x, 135, 280 - 135); // Pass slider bounds as parameters
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
                    key.setMouseLeftClick(false);
                }
            }
        });

        // Mouse motion listener to track mouse position
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint(); // Update mouse position
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePosition = e.getPoint(); // Update mouse position while dragging
                int mouseX = e.getX();
                int mouseY = e.getY();

                // Vị trí thanh trượt
                int sliderX = 100;
                int sliderY = 65;
                int sliderWidth = 150;
                int sliderHeight = 20;

                // Kiểm tra nếu chuột kéo qua thanh trượt
                if (mouseY >= sliderY && mouseY <= sliderY + sliderHeight) {
                    updateVolume(mouseX, sliderX, sliderWidth);
                }
            }
        });

        // Key listeners for keyboard input
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                isController=false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A -> key.setKey_left(true);
                    case KeyEvent.VK_D -> key.setKey_right(true);
                    case KeyEvent.VK_S -> key.setKey_down(true);
                    case KeyEvent.VK_W -> key.setKey_up(true);
                    case KeyEvent.VK_ENTER -> key.setKey_enter(true);
                    case KeyEvent.VK_1 -> key.setKey_1(true);
                    case KeyEvent.VK_2 -> key.setKey_2(true);
                    case KeyEvent.VK_3 -> key.setKey_3(true);
                    case KeyEvent.VK_4 -> key.setKey_4(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A -> key.setKey_left(false);
                    case KeyEvent.VK_D -> key.setKey_right(false);
                    case KeyEvent.VK_S -> key.setKey_down(false);
                    case KeyEvent.VK_W -> key.setKey_up(false);
                    case KeyEvent.VK_ENTER -> key.setKey_enter(false);
                }
            }
        });

        // Main game loop thread
        new Thread(() -> {
            long lastShotTime = 0; // Thêm biến để theo dõi thời gian bắn cuối cùng
            while (start) {
                if (player.isAlive()) {
                    float speed = 1f; // Movement speed
                    // Player movement
                    if (key.isKey_left()) {
                        if ((!map.checkCollision(player).contains("left")) ) {
                            player.changeLocation(player.getX () - speed, player.getY());
                        }
                        else {
                            System.out.println("left is prevent");
                        }
                    }
                    if (key.isKey_right()) {
                        if (!map.checkCollision(player).contains("right")) {
                            player.changeLocation(player.getX() + speed, player.getY());
                        }
                        else{
                            System.out.println("right is prevent");
                        }
                    }
                    if (key.isKey_up()) {
                        if (!map.checkCollision(player).contains("up")) {
                            player.changeLocation(player.getX(), player.getY() - speed);
                        }
                        else {
                            System.out.println("up is prevent");
                        }
                    }
                    if (key.isKey_down()) {
                        if (!map.checkCollision(player).contains("down")) {
                            player.changeLocation(player.getX(), player.getY() + speed);
                        }
                        else {
                            System.out.println("down is prevent");
                        }
                    }

                    // Rotation logic (calculate angle between player and mouse)
                    if (mousePosition != null) {
                        double dx = mousePosition.x - player.getX();
                        double dy = mousePosition.y - player.getY();

                        // Calculate angle between player and mouse
                        double angleToMouse = Math.toDegrees(Math.atan2(dy, dx));

                        // Ensure angle is between 0 and 360
                        if (angleToMouse < 0) {
                            angleToMouse += 360;
                        }

                        // Update player's rotation angle
                        player.changeAngle((float) angleToMouse);
                    }
                    else{
                        System.out.println("mouse position is null");
                    }

                    checkItems();

                    // Gun selection
                    if (key.isKey_1() && GunList.size() > 0) {
                        gunEquip = GunList.get(0);
                    } else if (key.isKey_2() && GunList.size() > 1) {
                        gunEquip = GunList.get(1);
                    } else if (key.isKey_3() && GunList.size() > 2) {
                        gunEquip = GunList.get(2);
                    } else if (key.isKey_4() && GunList.size() > 3) {
                        gunEquip = GunList.get(3);
                    }

                    // Shooting logic with time-based cooldown
                    long currentTime = System.currentTimeMillis();
                    if (key.isMouseLeftClick() && (mousePosition.x > 280 || mousePosition.x < 135 || mousePosition.y > 105 || mousePosition.y < 85)) {
                        // Kiểm tra thời gian cooldown
                        int cooldownTime = 0;
                        switch (gunEquip.getName()) {
                            case "pistol" -> cooldownTime = 300;
                            case "rifle" -> cooldownTime = 150;
                            case "sniper" -> cooldownTime = 1000;
                            case "grenade" -> cooldownTime = 2000;
                        }

                        // Chỉ bắn nếu đã qua thời gian cooldown
                        if (currentTime - lastShotTime >= cooldownTime) {
                            bullets.add(gunEquip.shoot(player.getX(), player.getY(), player.getAngle(),
                                    20, 8, gunEquip.getName().toString(), Sound.getVolume()));
                            lastShotTime = currentTime; // Cập nhật thời gian bắn cuối
                        }
                    }
                }
                else {
                    if (key.isKey_enter()) {
                        // Reset key states to prevent sticky keys
                        key.setKey_1(false);
                        key.setKey_2(false);
                        key.setKey_3(false);
                        key.setKey_4(false);
                        key.setMouseLeftClick(false);

                        // Reset game
                        resetGame();
                    }
                }

                // Enemy logic
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                    if (enemy != null) {
                        enemy.updateMovement(
                                player.getX(),
                                player.getY(),
                                map.isEnemyCollidingWithWall(enemy)
                        );
                        if (!enemy.check(width, height)) {
                            enemies.remove(i);
                            System.out.println("removed");
                        } else {
                            if (player.isAlive()) {
                                checkPlayer(enemy);
                            }
                        }
                    }
                }

                sleep(5); // Sleep for 5ms to reduce CPU load
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

                    if(bullet.type==4){
                        addExplosion(bullet.getX(), bullet.getY(), loadImage("/game/image/explode.png"), 3000);
                        renderExplosions(g2,bullet);
                    }

                    boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(),3, 5, 60, 0.5f, new Color(230, 207, 105)));
                    // Cập nhật HP của kẻ thù dựa trên kích thước viên đạn, nếu HP = 0
                    if(!enemy.hpEnemey.updateHP(bullet.getDamage())) {
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



    public void Explodesion(double doubleX, double doubleY, Image image){
        // Tải hình ảnh bản đồ nếu chưa được tải
        Image imageExplode = image;
        int timeCount=0;
        // Vẽ hình ảnh bản đồ lên nền
        while (timeCount<3000) {
            if (imageExplode != null) {
                g2.drawImage(imageExplode, (int) doubleX, (int) doubleY, width * 3, height * 3, null); // Vẽ hình ảnh với kích thước của panel
            } else {
                g2.setColor(new Color(30, 30, 30)); // Màu nền xám đậm nếu không tải được hình ảnh
                g2.fillRect(0, 0, width, height); // Vẽ hình chữ nhật với kích thước của panel
            }
            timeCount+=1;
        }
    }

    private void checkPlayer(Enemy enemy) {
        if (enemy.type == 2) {
            // tinh khoảng cách
            double distanceX = Math.abs(player.getX() - enemy.getX());
            double distanceY = Math.abs(player.getY() - enemy.getY());
            double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            double phamvi = 200.0;

            //nếu đứng gần ng chơi thì kích hoạt
            if (distance <= phamvi) {
                enemies.remove(enemy);
                Sound.soundBoom();

                double x = enemy.getX() + Enemy.ENEMY_SIZE / 2;
                double y = enemy.getY() + Enemy.ENEMY_SIZE / 2;

                boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));

                player.hpPlayer.updateHP(20);
            }
        }

        if (enemy != null) {
            // Tạo vùng giao nhau giữa hình dạng của player và enemy
            Area area = new Area(player.getShape());
            area.intersect(enemy.getShape());

            // Kiểm tra nếu có va chạm (vùng giao nhau không rỗng)
            if (!area.isEmpty()) {
                // Lấy máu của enemy
                double enemyHp = enemy.hpEnemey.getHP();

                // Lấy thời gian hiện tại
                long currentTime = System.currentTimeMillis();

                // Chỉ xử lý va chạm nếu đã qua 3 giây kể từ lần va chạm trước
                if (currentTime - lastExplosionTime >= 3000) {
                    // Kiểm tra nếu enemy chết khi va chạm với player
                    if (!enemy.hpEnemey.updateHP(player.hpPlayer.getHP())) {
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
                    if (!player.hpPlayer .updateHP(enemyHp)) {
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
            g2.drawImage(imagemap, -1200, -900, width * 3, height * 3, null); // Vẽ hình ảnh với kích thước của panel
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
                        if (map.isBulletCollidingWithWall(bullet)){
                            synchronized (bullets) {
                                bullets.remove(bullet);
                            }
                            continue;
                        }
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
        try {
        if (player.isAlive()) {
            player.draw(g2); // Vẽ player
            //player.drawBorder(g2);
            gunEquip.drawGun(g2,player.getX(),player.getY(), player.getAngle(), gunEquip.getName());
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy != null) {
                enemy.draw(g2); // Vẽ từng kẻ thù
                //enemy.drawBorder(g2); // Vẽ từng kẻ thù

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


        }
        catch (Exception exception){
            System.out.println(exception.getMessage());
        };


        //hiển thị trạng thái
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Player: " + playerName, 30, 40);
        g2.drawString("Score: "+score, 30, 60);
        g2.drawString("Ammo: " + gunEquip.getCurrentAmmo(), 30, 80);

        drawVolumeControl();
        drawMinimap();
        map.draw(g2);


        if(!player.isAlive()){
            // Calculate and save final time if not already done
            if (finalElapsedTime == null) {
                long endTime = System.nanoTime();
                finalElapsedTime = (endTime - main.getStartTime()) / 1000000000;
                savePlayerScore();
            }


            // Gradient background
            GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 0, 0, 200), 0, height, new Color(50, 50, 50, 150));
            Graphics2D g2d = (Graphics2D) g2;
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);

// "GAME OVER" title
            g2.setFont(new Font("Arial", Font.BOLD, 80));
            g2.setColor(Color.RED);
            g2.drawString("GAME OVER", width / 2 - 210, height / 2 - 180);

// Player info and top players box
            g2.setColor(new Color(30, 30, 30, 200));
            g2.fillRoundRect(width / 2 - 300, height / 2 - 120, 700, 450, 20, 20);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(width / 2 - 300, height / 2 - 120, 700, 450, 20, 20);

// Player score, time, and name
            g2.setFont(new Font("Arial", Font.PLAIN, 30));
            g2.setColor(Color.YELLOW);
            g2.drawString("Score: " + score, width / 2 - 260, height / 2 - 60);
            g2.setColor(Color.GREEN);
            try {
                g2.drawString("Time Played: " + (finalElapsedTime / 60) + "m " + (finalElapsedTime % 60) + "s", width / 2 - 260, height / 2 );
            }
            catch (Exception exception){
                System.out.println(exception.getMessage());
            }
            g2.setColor(Color.CYAN);
            g2.drawString("Player: " + playerName, width / 2 - 260, height / 2 + 100);

            displayTop5Players(g2);
// Restart instructions
            g2.setFont(new Font("Arial", Font.BOLD, 25));
            g2.setColor(Color.WHITE);
            g2.drawString("Press Enter to restart game!", width / 2 - 110, height / 2 + 300);
        }
    }

    // Kiểm tra chạm item
    private void checkItems() {
        // Dùng Iterator để tránh ConcurrentModificationException
        synchronized (items) { // Sử dụng đồng bộ nếu `items` được chia sẻ giữa các luồng
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
                        item.collect(); // Đánh dấu item đã được thu thập

                        if (item.getType() != 3) { // Item không phải loại hồi máu
                            int randomGun = (int) (Math.random() * GunList.size()); // Random súng từ danh sách
                            int randomBullet = (int) (Math.random() * 30) + 1; // Random số đạn (1 - 30)
                            GunList.get(randomGun).addCurrentAmmo(randomBullet);
                        } else { // Nếu item là loại hồi máu
                            player.hpPlayer.restoreHP(20); // Hồi máu cho người chơi
                        }

                        // Xóa item khỏi danh sách một cách an toàn
                        iterator.remove();
                    }
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