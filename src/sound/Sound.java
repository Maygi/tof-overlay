package sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import model.MainDriver;

/**
 * An enum to handle sound effects, including volumes and file names.
 * @author May
 */
public enum Sound {
	
    PAUSE("pause"),
    RESET("reset"),
    SELECT("select", -8),
    STATUS_WARNING("statusalert"),
    HOVER("hover"),
    PROC("proc"),
    PROC2("proc2", 6),
    OK("ok"),
    QUEUEPOP("queuepop"),
    QUEUETICK("queuetick", -5),
    STATUS_WARNING2("warning", -5);
    
    /**
     * A clip to play the sound effect.
     */
    private Clip myClip;
    
    private int myVolume;
    
    /**
     * Initializes the sound.
     * @param theName The name of the sound.
     */
    Sound(final String theName) {
        try {
            final AudioInputStream stream = AudioSystem.getAudioInputStream(new File(
                                                     "sound/" + theName + ".wav"));
            myClip = AudioSystem.getClip();
            myClip.open(stream);
            myVolume = 0;
        } catch (final IOException | LineUnavailableException
                        | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the sound with a set volume.
     * @param theName The name of the sound.
     * @param theVolume The volume.
     */
    Sound(final String theName, final int theVolume) {
        try {
            final AudioInputStream stream = AudioSystem.getAudioInputStream(new File(
                                                     "sound/" + theName + ".wav"));
            myClip = AudioSystem.getClip();
            myClip.open(stream);
            myVolume = theVolume;
        } catch (final IOException | LineUnavailableException
                        | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes all the sound files.
     */
    public static void initialize() {
        values();
    }
    
    /**
     * Plays the sound at the default volume.
     */
    public void play() {
    	play(myVolume);
    }
    
    /**
     * Plays the sound at the given volume.
     * @param theVolume The given volume to play at.
     */
    public void play(final int theVolume) {
    	if (MainDriver.mute)
    		return;
        if (myClip.isRunning()) {
            myClip.stop();
        }
        myClip.setFramePosition(0); //reset the player
        final FloatControl gain = (FloatControl) myClip.getControl(
                                        FloatControl.Type.MASTER_GAIN);
        gain.setValue(theVolume); //set the volume loss
        myClip.start();
    }
    
}
