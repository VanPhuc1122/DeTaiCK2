package ViewClient;
import ViewClient.SendFile;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
public enum SoundEffect {
    
    MessageReceive("/audio/Ring.wav", false), 
    FileSharing("/audio/boing.wav", false);
    private Clip clip;
    private boolean loop;
    
    SoundEffect(String filename, boolean loop){
        try {
            this.loop = loop;
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIS = AudioSystem.getAudioInputStream(url);
            
            clip = AudioSystem.getClip();
            clip.open(audioIS);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("[SoundEffect]" +e.getMessage());
        }
    }
    
    public void play(){
        if(clip.isRunning()){
            clip.stop(); 
        }
      
        clip.setFramePosition(0);
        clip.start();
      
        if(loop){
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public void stop(){
        if(clip.isRunning()){
            clip.stop(); //   Stop Audio
        }
    }
}