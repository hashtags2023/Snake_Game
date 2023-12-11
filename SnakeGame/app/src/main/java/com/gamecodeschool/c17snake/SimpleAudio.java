/**
 * Author: Lori Yaniro
 * Date: 11/21/23
 * Description: This file contains the SimpleAudio class implementing the Audio interface.
 *              This class provides a basic implementation of audio-related functionality.
 *              You may extend or replace this class with more sophisticated implementations.
 */

package com.gamecodeschool.c17snake;

/**
 * SimpleAudio class implementing the Audio interface.
 * This class provides a basic implementation of audio-related functionality.
 * You may extend or replace this class with more sophisticated implementations.
 */
public class SimpleAudio implements Audio {

    private final SoundManager soundManager;

    /**
     * Constructor for SimpleAudio.
     *
     * @param soundManager The SoundManager instance to be used for audio operations.
     */
    public SimpleAudio(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    /**
     * Plays the sound associated with eating.
     * This method is part of the Audio interface implementation.
     */
    @Override
    public void playEatSound() {
        // Implementation for playing eat sound using soundManager
        soundManager.playEatSound();
    }

    /**
     * Plays the sound associated with crashing.
     * This method is part of the Audio interface implementation.
     */
    @Override
    public void playCrashSound() {
        // Implementation for playing crash sound using soundManager
        soundManager.playCrashSound();
    }
}
