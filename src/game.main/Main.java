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

    private void init(){
        setTitle("Zombies Doomday");
        setSize(1920, 1080); //kích thước window
        setLocationRelativeTo(null);
        setResizable(false); //không cho resize kích thước window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //khi thoát game thì dừng chương trình
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
        Main main = new Main() ;
        main.setVisible(true);
    }
}