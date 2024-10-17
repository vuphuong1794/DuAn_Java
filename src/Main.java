import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception{
        //window variables
        int tileSize = 32;
        int rows = 16;
        int cols = 16;
        int boardWith = tileSize * cols;
        int boardHeight = tileSize * rows;

        JFrame frame = new JFrame("Chicken Invaders");
        frame.setVisible(true);
    }
}