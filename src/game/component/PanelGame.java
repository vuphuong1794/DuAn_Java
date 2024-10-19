package game.component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

// Lớp này kế thừa JComponent để tạo một panel trò chơi tùy chỉnh
public class PanelGame extends JComponent {

    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private Thread thread;
    private boolean start = true;

    // Cài đặt FPS cho trò chơi
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS; // Thời gian mục tiêu cho mỗi khung hình tính bằng nanosecond

    // Phương thức để bắt đầu vòng lặp trò chơi
    public void start() {
        width = getWidth();
        height = getHeight();
        // Tạo một buffered image để thực hiện kỹ thuật double buffering
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        // Thiết lập gợi ý render để có chất lượng tốt hơn
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Tạo và bắt đầu thread vòng lặp trò chơi
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    long startTime = System.nanoTime();
                    drawBackground();
                    drawGame();
                    render();
                    // Tính toán thời gian ngủ để duy trì FPS ổn định
                    long time = System.nanoTime() - startTime;
                    if (time < TARGET_TIME) {
                        long sleep = (TARGET_TIME - time) / 1000000;
                        sleep(sleep);
                    }
                }
            }
        });
        thread.start();
    }

    // Phương thức để vẽ nền
    private void drawBackground() {
        g2.setColor(new Color(30, 30, 30)); // Màu xám đậm
        g2.fillRect(0, 0, width, height);
    }

    // Phương thức để vẽ các phần tử trò chơi (cần được triển khai)
    private void drawGame() {}

    // Phương thức để hiển thị buffered image lên màn hình
    private void render() {
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    // Phương thức hỗ trợ để xử lý việc ngủ của thread
    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}