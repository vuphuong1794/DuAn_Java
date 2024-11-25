package game.component;

import game.obj.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenuPanel extends JPanel {
    private final JButton startButton;
    private final JButton exitButton;
    private final JTextField playerNameField;
    private final JButton instructionsButton;
    private Image imagemap;
    private String name;

    public MainMenuPanel(ActionListener onStartGame, ActionListener onExitGame) {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK); // Set a dark background color to enhance contrast

        // Initialize buttons with styling
        startButton = new JButton("Bắt đầu trò chơi");
        startButton.setFont(new Font("Arial", Font.BOLD, 22));
        startButton.setBackground(new Color(50, 150, 50)); // Greenish button
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createBevelBorder(0));
        startButton.addActionListener(onStartGame);

        exitButton = new JButton("Thoát");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton.setBackground(new Color(200, 50, 50)); // Red button
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createBevelBorder(0));
        exitButton.addActionListener(onExitGame);

        // Initialize player name input field
        playerNameField = new JTextField(15);
        playerNameField.setFont(new Font("Arial", Font.PLAIN, 18));
        playerNameField.setBorder(BorderFactory.createTitledBorder("Nhập tên người chơi"));
        playerNameField.setBackground(new Color(240, 240, 240)); // Lighter background for input
        playerNameField.setForeground(Color.BLACK);

        // Instructions button
        instructionsButton = new JButton("Hướng dẫn");
        instructionsButton.setFont(new Font("Arial", Font.PLAIN, 18));
        instructionsButton.setBackground(new Color(100, 100, 255)); // Blue button
        instructionsButton.setForeground(Color.WHITE);
        instructionsButton.setFocusPainted(false);
        instructionsButton.setBorder(BorderFactory.createBevelBorder(0));
        instructionsButton.addActionListener(e -> showInstructions());

        // Layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Add top margin (empty space) to avoid overlapping the logo
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Ensures spacing affects all components
        add(Box.createRigidArea(new Dimension(0, 150)), gbc); // Adjust the top margin as needed

        // Player name input at the top
        gbc.gridy = 1;
        add(playerNameField, gbc);

        // Start button below name input
        gbc.gridy = 2;
        add(startButton, gbc);

        // Instructions button below start
        gbc.gridy = 3;
        add(instructionsButton, gbc);

        // Exit button at the bottom
        gbc.gridy = 4;
        add(exitButton, gbc);

        // Load background image
        imagemap = loadImage("/game/image/menu.png");
    }

    private Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showInstructions() {
        JOptionPane.showMessageDialog(this,
                "1. Sử dụng các phím W, A, S, D để di chuyển.\n" +
                        "2. Nhấn 'Chuột Trái' để bắn.\n" +
                        "3. Di chuyển tới vật phẩm rơi trên bản đồ để nhặt / sử dụng.\n" +
                        "4. Tránh zombies và sống sót càng lâu càng tốt.",
                "Hướng dẫn chơi",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();

        Graphics2D g2 = (Graphics2D) g;

        if (imagemap != null) {
            g2.drawImage(imagemap, 0, 0, width, height, null);
        } else {
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, width, height);
        }
    }

    public void getPlayerName() {
        Player ngchoi = new Player();
        name =  playerNameField.getText();
        ngchoi.setPlayerName(name);
    }
}
