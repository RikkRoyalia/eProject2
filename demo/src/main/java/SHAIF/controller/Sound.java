package SHAIF.controller;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class Sound {
    Clip clip;
    URL soundUrl[] = new URL[10];

    public Sound() {
        soundUrl[0] = getClass().getResource("/sound/Adventure.wav");
        soundUrl[1] = getClass().getResource("/sound/SH-Song.wav");
        soundUrl[2] = getClass().getResource("/sound/hitmonster.wav");
        soundUrl[3] = getClass().getResource("/sound/receivedamage.wav");
        soundUrl[4] = getClass().getResource("/sound/gameover.wav");
        soundUrl[5] = getClass().getResource("/sound/powerup.wav");
    }

    public void setFile(int i) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl[i]);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void play() {
        clip.start();
    }

    public void stop() {
        clip.stop();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void playMusic(int i){
        setFile(i);
        play();
        loop();
    }

    public void stopMusic() {
        stop();
    }

    public void playSE(int i) {
        setFile(i);
        play();
    }
}
