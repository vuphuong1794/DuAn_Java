package game.main;

import game.component.PanelGame;
import game.component.MainMenuPanel;
import game.obj.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 940;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private PanelGame panelGame;
    private Player player;
    private long startTime;

    public Main() {
        init();
    }

    private void init() {
        setTitle("Zombies Doomsday");

        //lấy kích thước màn hình của người dùng
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        //set kích thước game bằng với độ phân giải màn hình của người dùng
        setSize((int) width,(int) height);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Khởi tạo CardLayout để chuyển đổi giữa các panel khác nhau.
        cardLayout = new CardLayout();
        //Khởi tạo mainPanel với CardLayout để quản lý các màn hình khác nhau.
        mainPanel = new JPanel(cardLayout);

        player = new Player();  // khoi tao player

        //Khởi tạo panel game và truyền đối tượng player vào panel game. this là đối tượng Main, được dùng để tham chiếu tới frame chính.
        panelGame = new PanelGame(player, this);

        // Tạo một đối tượng MainMenuPanel, là màn hình menu của game, và truyền đối tượng player vào.
        MainMenuPanel menuPanel = createMenuPanel(player);

        // Add panels to CardLayout
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(panelGame, "Game");

        add(mainPanel);
        cardLayout.show(mainPanel, "Menu");

        // Optional: Handle window opening events (preload resources)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {

            }
        });
    }

    private MainMenuPanel createMenuPanel(Player player) {
        // Pass the player object to the MainMenuPanel constructor
        return new MainMenuPanel(player,
                e -> startGame(),  // Start the game when button is clicked
                e -> System.exit(0)  // Exit the application when button is clicked
        );
    }

    private void startGame() {
        startTime = System.nanoTime();
        cardLayout.show(mainPanel, "Game");

        // chaỵ panelgame với obj player
        SwingUtilities.invokeLater(() -> {
            panelGame.start(player);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mainApp = new Main();
            mainApp.setVisible(true);
        });
    }

    public long getStartTime() {
        return startTime;
    }
}
