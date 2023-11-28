package com.gamecodeschool.c17snake;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

class SnakeGame extends SurfaceView implements Runnable{

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;


    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private final int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    private final SurfaceHolder mSurfaceHolder;
    private final Paint mPaint;

    // A snake ssss
    private final Snake mSnake;
    // And an apple
    private final Apple mApple;
    private final SoundManager soundManager;
    private int pauseButtonX;
    private int pauseButtonY;
    private int pauseButtonSize = 100;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size , SoundManager soundManager) {
        super(context);

        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;
        pauseButtonX = size.x - pauseButtonSize - 50; // Adjust the margin as needed
        pauseButtonY = 30;
        // Initialize the SoundPool

         this.soundManager = soundManager;

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game object

        mApple = new Apple.AppleBuilder(context)
                .setSpawnRange(new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh))
                .setSize(blockSize)
                .setType(AppleType.GOOD)
                .build();

// Create a bad apple with a different bitmap
        Bitmap badAppleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bad_apple);
        badAppleBitmap = Bitmap.createScaledBitmap(badAppleBitmap, blockSize, blockSize, false);

        //Use Bad Apple as per requirement
        Apple badApple = new Apple.AppleBuilder(context)
                .setSpawnRange(new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh))
                .setSize(blockSize)
                .setType(AppleType.BAD)
                .setBitmap(badAppleBitmap)
                .build();

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

    }


    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        mApple.move();

        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
    }


    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
        }
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
            mApple.move();

            // Add to  mScore
            mScore = mScore + 1;

            // Play a sound
            soundManager.playEatSound();
        }

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            soundManager.playCrashSound();

            mPaused =true;
        }

    }


    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            // Objects for drawing
            Canvas mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(getColor("BackgroundColor"));
            mPaint.setColor(getColor("White"));
            mPaint.setTextSize(120);
            mCanvas.drawText("" + mScore, 20, 120, mPaint);
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);
            // Draw the pause button using an icon
            // Draw the pause button using an icon
            Drawable pauseIconDrawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_pause, null);
            if (pauseIconDrawable != null) {
                pauseIconDrawable.setBounds(pauseButtonX, pauseButtonY, pauseButtonX + pauseButtonSize, pauseButtonY + pauseButtonSize);
                pauseIconDrawable.draw(mCanvas);
            }

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
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
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

//        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
//            if (mPaused) {
//                mPaused = false;
//                newGame();
//                return true;
//            }
//            mSnake.switchSnakeMovement(motionEvent);
//        }
//        return true;
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
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }


    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}
