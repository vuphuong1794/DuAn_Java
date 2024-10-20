package game.main;

import game.component.PanelGame;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {

    public Main() {
        init();
    }

    private void init() {
        setTitle("Zombies Doomsday");
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        PanelGame panelGame = new PanelGame();
        add(panelGame);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                panelGame.start();
            }
        });
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}