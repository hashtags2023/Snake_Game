/*
 * AudioContext.java
 * Author: Lori Yaniro
 * Created on: November 21, 2023
 * Description: This class serves as the context for managing audio functionality in the Snake game.
 * It provides an abstraction layer for various audio strategies, allowing dynamic switching
 * between different audio implementations. The AudioContext delegates audio-related operations
 * to an implementation of the AudioStrategy interface.
 */
package com.gamecodeschool.c17snake;

public class AudioContext {
    private Audio audio;

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public void playEatSound() {
        audio.playEatSound();
    }

    public void playCrashSound() {
        audio.playCrashSound();
    }
    // Add more methods if needed for other audio types
}

