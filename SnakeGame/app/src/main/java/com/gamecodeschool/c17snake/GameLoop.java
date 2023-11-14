package com.gamecodeschool.c17snake;

public class GameLoop {

    private static final long TARGET_FPS = 10;
    private static final long MILLIS_PER_SECOND = 1000;

    public static void gameLoop(SnakeGame game) {
        while (game.isPlaying()) {
            if(!game.isPaused()) {
                // Update 10 times a second
                if (game.updateRequired()) {
                    game.update();
                }
            }

            game.draw();
        }
    }
}
