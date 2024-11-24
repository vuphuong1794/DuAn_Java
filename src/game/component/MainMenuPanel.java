package game.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenuPanel extends JPanel {
    private final JButton startButton;
    private final JButton exitButton;

    public MainMenuPanel(ActionListener onStartGame, ActionListener onExitGame) {
        setLayout(new GridBagLayout());
        setBackground(Color.DARK_GRAY);

        // Tạo nút "Bắt đầu"
        startButton = new JButton("Bắt đầu trò chơi");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.addActionListener(onStartGame);

        // Tạo nút "Thoát"
        exitButton = new JButton("Thoát");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton.addActionListener(onExitGame);

        // Thêm các nút vào giao diện
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(startButton, gbc);

        gbc.gridy = 1;
        add(exitButton, gbc);
    }
}
