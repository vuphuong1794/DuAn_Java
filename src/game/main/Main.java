package game.main;

import game.component.PanelGame;
import game.component.MainMenuPanel;
import game.obj.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private PanelGame panelGame;
    private Player player;

    public Main() {
        init();
    }

    private void init() {
        setTitle("Zombies Doomsday");
        setSize(1920, 940);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // CardLayout to switch between menu and game
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create the menu screen
        MainMenuPanel menuPanel = new MainMenuPanel(
                e -> showGameScreen(), // Event for "Start"
                e -> System.exit(0)    // Event for "Exit"
        );

        // Create the game screen
        panelGame = new PanelGame();

        // Add screens to CardLayout
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(panelGame, "Game");

        add(mainPanel);

        // Show the menu initially
        cardLayout.show(mainPanel, "Menu");

        // Add a listener to start the game when the window opens
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Preload resources if necessary
            }
        });
    }


    private void showGameScreen() {

        cardLayout.show(mainPanel, "Game");

        // Start the game with the player object
        SwingUtilities.invokeLater(() -> {
            panelGame.start(player);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}
