package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.MediaPlayer;

class SnakeGame extends SurfaceView implements Runnable{
    private final AudioContext audioContext = new AudioContext();

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    private boolean gameOver = false;


    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private final int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    private final SurfaceHolder mSurfaceHolder;
    private final Paint mPaint;

    // A snake ssss
    private final Snake mSnake;
    // An apple
    private final Apple mApple;

    // Obstacle
    private Obstacle mObstacle;
    private final SoundManager soundManager;

    // MediaPlayer for background music
    private MediaPlayer mediaPlayer;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size , SoundManager soundManager) {
        super(context);

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
        this.soundManager = soundManager;

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Initialize the MediaPlayer for background music
        mediaPlayer = MediaPlayer.create(context, R.raw.backgroundmusic);
        mediaPlayer.setLooping(true);

        // Set the default audio strategy
        audioContext.setAudio(new SimpleAudio());
    }
    // Call the constructors of our two game objects
    mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                       mNumBlocksHigh),
    blockSize);

    mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                       mNumBlocksHigh),
    blockSize);

}

    // Called to start a new game
    public void newGame() {

        NewGame.startGame(this);
    }

    // Handles game over
    public void gameOver() {
        gameOver = true;
    }

    // Handles the game loop
    @Override
    public void run() {

        GameLoop.gameLoop(this);
    }

    // Check to see if it is time for an update
    public boolean updateRequired() {

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }

    // Update all the game objects
    public void update() {

        // Move the snake
        mSnake.move();

        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mApple.getLocation())){
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!
            mApple.spawn();

            // Add to  mScore
            mScore = mScore + 1;

            // Play a sound
            soundManager.playEatSound();
        }

        /* Play sounds using the AudioContext */
        audioContext.playEatSound();
        audioContext.playCrashSound();

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            soundManager.playCrashSound();

            mPaused = true;

            // Set the game to game over state after death
            gameOver();
        }

    }

    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            // Objects for drawing
            Canvas mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(ColorHelper.getColor("BackgroundColor"));
            mPaint.setColor(ColorHelper.getColor("White"));
            mPaint.setTextSize(120);
            mCanvas.drawText("" + mScore, 20, 120, mPaint);
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);
            // Draw the obstalce
            mObstacle.draw(mCanvas);

            // Check if game is over
            if (gameOver) {
                mCanvas.drawText("Game Over!", 200, 300, mPaint);
                mCanvas.drawText("Final Score: " + mScore, 200, 400, mPaint);
                mCanvas.drawText("Tap to Restart", 200, 500, mPaint);
            }
            // If game is not over, check if it is paused
            if(mPaused){
                mPaint.setColor(getColor("White"));
                mPaint.setTextSize(250);
                mCanvas.drawText(getResources().
                                getString(R.string.tap_to_play),
                        200, 700, mPaint);
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }
    private int getColor(String colorName){

        switch (colorName){
            case "BackgroundColor":
                return Color.argb(255, 26, 128, 182);
            case "White":
                return Color.argb(255, 255, 255, 255);

        }
        return 0;
    }

    // Touch event handing
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (gameOver) {
                gameOver = false;
                mPaused = false;
                newGame();
                return true;
            } else if (mPaused) {
                mPaused = false;
                newGame();
                return true;
            }

            mSnake.switchSnackMovement(motionEvent);
        } else if (game)
            return true;
    }

    // Pause the game
    public void pause() {
        mPlaying = false;
        try {
            mediaPlayer.pause(); // Pause background music
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    // Resume the game
    public void resume() {
        mPlaying = true;
        mediaPlayer.start(); // Start or resume background music
        mThread = new Thread(this);
        mThread.start();
    }

    // Check if the game is playing
    public boolean isPlaying() {
        return mPlaying;
    }
    // Check if the game is paused
    public boolean isPaused() {
        return mPaused;
    }
    // Getter for Snake
    public Snake getSnake() {
        return mSnake;
    }
    // Getter for Apple
    public Apple getApple() {
        return mApple;
    }
    // Setter for the score
    public void setScore(int num) {
        mScore = num;
    }
    // Setter for the next frame time
    public void setmNextFrameTime(long l) {
        mNextFrameTime = l;
    }

    public int getNumBlocksWide() {
        return NUM_BLOCKS_WIDE;
    }

    public int getNumBlocksHigh() {
        return mNumBlocksHigh;
    }
}
