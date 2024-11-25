package game.component;

import game.obj.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MainMenuPanel extends JPanel {
    private final JTextField playerNameField;
    private final JButton startButton;
    private final JButton exitButton;
    private final JButton instructionsButton;
    private Player player;
    private Image backgroundImage;

    public MainMenuPanel(ActionListener onStartGame, ActionListener onExitGame, Player player) {
        this.player = this.player;
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        // Initialize components
        playerNameField = new JTextField(15);
        playerNameField.setFont(new Font("Arial", Font.PLAIN, 18));
        playerNameField.setBorder(BorderFactory.createTitledBorder("Nhập tên người chơi"));
        playerNameField.setBackground(new Color(240, 240, 240));
        playerNameField.setForeground(Color.BLACK);

        // Add document listener to update player's name dynamically
        playerNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePlayerName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePlayerName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePlayerName();
            }

            private void updatePlayerName() {
                MainMenuPanel.this.player.setPlayerName(playerNameField.getText());
            }
        });

        startButton = createStyledButton("Bắt đầu trò chơi", new Color(50, 150, 50), Color.WHITE, 22, onStartGame);
        exitButton = createStyledButton("Thoát", new Color(200, 50, 50), Color.WHITE, 20, onExitGame);

        instructionsButton = createStyledButton("Hướng dẫn", new Color(100, 100, 255), Color.WHITE, 18, e -> showInstructions());

        // Layout configuration
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridy = 0;
        add(Box.createRigidArea(new Dimension(0, 150)), gbc);

        gbc.gridy = 1;
        add(playerNameField, gbc);

        gbc.gridy = 2;
        add(startButton, gbc);

        gbc.gridy = 3;
        add(instructionsButton, gbc);

        gbc.gridy = 4;
        add(exitButton, gbc);

        // Load background image
        backgroundImage = loadImage("/game/image/menu.png");
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor, int fontSize, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createBevelBorder(0));
        button.addActionListener(action);
        return button;
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
        Graphics2D g2 = (Graphics2D) g;

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public String getPlayerName() {
        return playerNameField.getText();
    }
}
