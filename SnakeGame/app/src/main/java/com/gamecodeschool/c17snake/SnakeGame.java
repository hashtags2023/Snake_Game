package com.gamecodeschool.c17snake;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.MediaPlayer;
import android.view.View;
import java.util.List;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

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

    // HighScore feature
    private int mHighscore;
    private SharedPreferences prefs;

    // Leaderboard feature
    private LeaderboardManager leaderboardManager;

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

    private int pauseButtonX;
    private int pauseButtonY;
    private int pauseButtonSize = 100;
    private PowerUp powerUp;

    long TARGET_FPS = 10;

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

        powerUp = new PowerUp(new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),getContext() , blockSize);

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Initialize the MediaPlayer for background music
        mediaPlayer = MediaPlayer.create(context, R.raw.backgroundmusic);
        mediaPlayer.setLooping(true);

        // Initialize the highscore from SharedPreferences
        prefs = context.getSharedPreferences("HighScorePrefs", Context.MODE_PRIVATE);
        mHighscore = prefs.getInt("highscore", 0);

        // Initialize LeaderboardManager
        leaderboardManager = new LeaderboardManager(context);

        // Set the default audio strategy
        audioContext.setAudio(new SimpleAudio(soundManager));

    // Call the constructors of our two game objects
    mApple = new Apple(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);

    mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                       mNumBlocksHigh),
    blockSize);

    }

    // Called to start a new game
    public void newGame() {

        NewGame.startGame(this);
        powerUp.reset();
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

            if (mScore > mHighscore) {
                mHighscore = mScore;

                // Save highscore to preferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("highscore", mHighscore);
                editor.apply();
            }

            if (!powerUp.isPowerUpShowing){
                powerUp.move();
                powerUp.EnableToShowPowerUp();
            }

            // Play a sound
            soundManager.playEatSound();
        }
        else if(mSnake.checkDinner(powerUp.getLocation())){

            powerUp.move();
            powerUp.hidePowerUp();
            switch (powerUp.type){
                case SPEED_UP:
                    TARGET_FPS = 15;
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TARGET_FPS = 10;
                        }
                    },5000);
                    break;
                case INVULNERABILITY:
                    mSnake.isSnakeHasPowerOfINVULNERABILITY = true;
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSnake.isSnakeHasPowerOfINVULNERABILITY =false;
                        }
                    },5000);
                    break;
            }
            powerUp.setSnakeHasPower();
            soundManager.playEatSound();

        }

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            soundManager.playCrashSound();

            // Update leaderboard
            leaderboardManager.updateLeaderboard(mScore);

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

            // Display the Highscore
            mCanvas.drawText("Highscore: " + mHighscore,20,240,mPaint);

            // Draw the obstalce
            // mObstacle.draw(mCanvas);

            if (powerUp.isPowerUpShowing){
                powerUp.draw(mCanvas,mPaint);
            }

            Drawable pauseIconDrawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_pause, null);
            if (pauseIconDrawable != null) {
                pauseIconDrawable.setBounds(pauseButtonX, pauseButtonY, pauseButtonX + pauseButtonSize, pauseButtonY + pauseButtonSize);
                pauseIconDrawable.draw(mCanvas);
            }

            // Check if game is over
            if (gameOver) {
                mCanvas.drawText("Game Over!", 800, 300, mPaint);
                mCanvas.drawText("Final Score: " + mScore, 800, 400, mPaint);
                mCanvas.drawText("Tap to Restart", 800, 500, mPaint);
            }
            // If game is not over, check if it is paused
            if(mPaused) {
                mPaint.setColor(getColor("White"));
                mPaint.setTextSize(250);
                mCanvas.drawText(getResources().
                                getString(R.string.tap_to_play),
                        700, 700, mPaint);

                // Display the leaderboard
                mPaint.setTextSize(60);
                mPaint.setColor(getColor("White"));
                mCanvas.drawText("Leaderboard", 20, 400, mPaint);

                List<Integer> leaderboard = leaderboardManager.getLeaderboard();
                int yPos = 500;

                for (int i = 0; i < leaderboard.size(); i++) {
                    mCanvas.drawText((i + 1) + ". " + leaderboard.get(i), 20, yPos, mPaint);
                    yPos += 80;
                }
            }

            if (gameOver && !mPaused) {
                newGame();
                gameOver = false;
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
 /*       if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
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

            mSnake.switchSnakeMovement(motionEvent);
        } else if (game) {
            return true;
        }*/
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        if ((motionEvent.getAction() & MotionEvent.ACTION_UP) == MotionEvent.ACTION_UP) {
            if (mPaused) {
                mPaused = false;
                newGame();

                return true;
            } else if (isClickPauseButton(touchX, touchY)) {
                pause();
                showResumeDialog();
            } else {
                mSnake.switchSnakeMovement(motionEvent);
            }
            return true;
        }
        return true;
    }

    private void showResumeDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dilaog_resume);
        dialog.findViewById(R.id.newGameButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.resumeButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resume();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean isClickPauseButton(float x, float y) {
        return x >= pauseButtonX && x <= pauseButtonX + pauseButtonSize &&
                y >= pauseButtonY && y <= pauseButtonY + pauseButtonSize;
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
