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
    private Player player;  // Player object

    public Main() {
        init()
        ;
    }

    private void init() {
        setTitle("Zombies Doomsday");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        setSize((int) width,(int) height);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Set up CardLayout for panel switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize the player object here
        player = new Player();  // Initialize player

        // Create the menu screen and pass the player object
        MainMenuPanel menuPanel = createMenuPanel(player);

        // Create the game screen
        panelGame = new PanelGame();

        // Add panels to CardLayout
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(panelGame, "Game");

        add(mainPanel);
        cardLayout.show(mainPanel, "Menu");

        // Optional: Handle window opening events (preload resources)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // You can load resources here if necessary (e.g., preload images)
            }
        });
    }

    private MainMenuPanel createMenuPanel(Player player) {
        // Pass the player object to the MainMenuPanel constructor
        return new MainMenuPanel(
                e -> startGame(),  // Start the game when button is clicked
                e -> System.exit(0),  // Exit the application when button is clicked
                player  // Pass player object
        );
    }

    private void startGame() {
        // Initialize player inside startGame to be sure player is ready
        if (player == null) {
            player = new Player();  // Initialize player if not already initialized
        }

        cardLayout.show(mainPanel, "Game");

        // Start the game with the player object
        SwingUtilities.invokeLater(() -> {
            panelGame.start(player);  // Pass player to the game panel
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mainApp = new Main();
            mainApp.setVisible(true);
        });
    }
}
