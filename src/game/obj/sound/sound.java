package game.obj.sound;
import javax.sound.sampled.*;
import java.net.URL;

public class sound {

    private final URL shoot;
    private final URL hit;
    private final URL zombie;
    private final URL shotgun;

    public sound(){
        this.shoot = this.getClass().getClassLoader().getResource("game/obj/sound/shoot.wav");
        this.hit = this.getClass().getClassLoader().getResource("game/obj/sound/hit.wav");
        this.zombie = this.getClass().getClassLoader().getResource("game/obj/sound/zombie.wav");
        this.shotgun = this.getClass().getClassLoader().getResource("game/obj/sound/shotgun.wav");
    }

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

    private void play(URL url){
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if(event.getType() == LineEvent.Type.STOP){
                        clip.close();
                    }
                }
            });
            audioIn.close();
            clip.start();
        }catch (Exception e){
            System.err.println(e);
        }
    }
}
