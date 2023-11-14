package com.gamecodeschool.c17snake;

public class NewGame {
    public static void startGame(SnakeGame game) {
        game.getSnake().reset(game.getNumBlocksWide(), game.getNumBlocksHigh());
        game.getApple().spawn();
        game.setScore(0);
        game.setmNextFrameTime(System.currentTimeMillis());
    }
}
