package game.obj.sound;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.URL;
import java.awt.Dimension;

public class sound {

    // URLs cho các âm thanh
    private final URL shoot;
    private final URL hit;
    private final URL zombie;
    private final URL shotgun;
    private final URL boom;

    // Biến điều chỉnh âm lượng
    private float volume = 0.5f;
    private JSlider volumeSlider;

    public sound(){
        // Tải các âm thanh
        this.shoot = this.getClass().getClassLoader().getResource("game/obj/sound/shoot.wav");
        this.hit = this.getClass().getClassLoader().getResource("game/obj/sound/hit.wav");
        this.zombie = this.getClass().getClassLoader().getResource("game/obj/sound/zombie.wav");
        this.shotgun = this.getClass().getClassLoader().getResource("game/obj/sound/shotgun.wav");
        this.boom = this.getClass().getClassLoader().getResource("game/obj/sound/boom.wav");
        
        // Tạo thanh trượt âm lượngds
        createVolumeControl();
    }

    // Tạo thanh điều khiển âm lượng
    private void createVolumeControl() {
        // Tạo thanh trượt
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        volumeSlider.setPreferredSize(new Dimension(150, 40));
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setMinorTickSpacing(5);

        // Xử lý sự kiện thay đổi thanh trượt để điều chỉnh âm lượng
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                volume = volumeSlider.getValue() / 100f;
            }
        });
    }

    // Trả về thanh trượt để có thể vẽ lên giao diện game
    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    // Phương thức để set âm lượng từ bên ngoài
    public void setVolume(float volume) {
            this.volume = volume;
        volumeSlider.setValue((int)(volume));
    }

    // Lấy âm lượng hiện tại
    public float getVolume() {
        return volume;
    }

    // Các phương thức phát âm thanh cho các hành động khác nhau
    public void soundShoot(){
        play(shoot);
    }
    
    public void soundHit(){
        play(hit);
    }
    
    public void soundZombie(){
        play(zombie);
    }
    
    public void soundShotgun(){
        play(shotgun);
    }

    public void soundBoom(){ play(boom); }

    // Phương thức chung để phát âm thanh
    private void play(URL url) {
        try {
            // Mở file âm thanh
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Điều chỉnh âm lượng xuống còn 10% của âm lượng thực tế
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB ;
            if (volume > 0) {
                dB = 20f * (float) Math.log10(volume * 0.3f);
            } else {
                dB = -80f; // Giá trị tối thiểu để làm im tiếng
            }
            gainControl.setValue(dB);

            // Lắng nghe sự kiện kết thúc âm thanh để đóng clip
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });

            clip.start(); // Phát âm thanh
        } catch (Exception e) {
            System.err.println("Không thể phát âm thanh: " + e);
        }
    }
}
