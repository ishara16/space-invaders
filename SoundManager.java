import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Small helper class that loads .wav files as resources (same folder as the
 * class files) and plays them. Handles missing files gracefully so the game
 * doesn't crash if a sound is missing — it just prints a warning and
 * continues silently. Also supports a global mute toggle (see the Settings
 * screen in SpaceInvaders).
 *
 * Usage:
 *   SoundManager.play("shoot.wav");           // one-shot sound effect
 *   SoundManager.loopMusic("background.wav"); // looping background music
 *   SoundManager.stopMusic();
 *   SoundManager.toggleMuted();                // flip mute on/off
 */
public class SoundManager {

    // Keep a single handle to the looping background music clip so we can
    // stop/restart it independently of the short sound effects.
    private static Clip musicClip;

    // Remembers the last music file requested, so we can resume it if the
    // user unmutes while a track "should" be playing.
    private static String currentMusicFile;

    private static boolean muted = false;

    /**
     * Plays a short sound effect once. A new Clip is opened each call so
     * that overlapping effects (e.g. rapid-fire shooting) don't cut each
     * other off. Does nothing while muted.
     */
    public static void play(String fileName) {
        if (muted) {
            return;
        }
        try {
            URL soundUrl = SoundManager.class.getResource("./" + fileName);
            if (soundUrl == null) {
                System.out.println("SoundManager: could not find " + fileName + " (skipping)");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            // Free up resources once playback finishes.
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioStream.close();
                    } catch (IOException ignored) {
                    }
                }
            });
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("SoundManager: failed to play " + fileName + " -> " + e.getMessage());
        }
    }

    /**
     * Starts looping background music. Stops any music already playing first.
     * Remembers the requested file even if muted, so unmuting later resumes it.
     */
    public static void loopMusic(String fileName) {
        currentMusicFile = fileName;
        stopMusic();
        if (muted) {
            return;
        }
        startMusicInternal(fileName);
    }

    private static void startMusicInternal(String fileName) {
        try {
            URL soundUrl = SoundManager.class.getResource("./" + fileName);
            if (soundUrl == null) {
                System.out.println("SoundManager: could not find " + fileName + " (skipping)");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("SoundManager: failed to loop " + fileName + " -> " + e.getMessage());
        }
    }

    /** Stops and releases the background music clip, if one is playing. */
    public static void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    /** Mutes or unmutes all sound. Unmuting resumes background music if a track was set. */
    public static void setMuted(boolean shouldMute) {
        muted = shouldMute;
        if (muted) {
            stopMusic();
        } else if (currentMusicFile != null) {
            startMusicInternal(currentMusicFile);
        }
    }

    public static void toggleMuted() {
        setMuted(!muted);
    }

    public static boolean isMuted() {
        return muted;
    }
}
