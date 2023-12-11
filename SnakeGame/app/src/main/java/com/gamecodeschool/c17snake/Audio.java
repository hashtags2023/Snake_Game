/**
 * Author: Lori Yaniro
 * Date: 11/21/21
 * Description: [Brief description of the purpose of this file or class]
 */
package com.gamecodeschool.c17snake;

/**
 * Audio interface representing the contract for audio-related functionality.
 * This follows the principles of Abstraction and Encapsulation.
 */
public interface Audio {

    /**
     * Plays the sound associated with eating.
     * This method should be implemented to play the sound effect
     * when the snake eats an apple.
     */
    void playEatSound();

    /**
     * Plays the sound associated with crashing.
     * This method should be implemented to play the sound effect
     * when the snake crashes.
     */
    void playCrashSound();

    // Additional methods can be commented similarly.
    // You can add more methods here for additional audio types, following the Open-Closed Principle.
    // For example:
    // void playPowerUpSound();
    // void playBackgroundMusic();

    // Ensure that the interface remains focused on audio-related functionality and follows the Single Responsibility Principle.
}

