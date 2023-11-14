package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;

class Apple {

    private final Point location = new Point();
    private final Point mSpawnRange;
    private final int mSize;
    private final Random random;
    private Bitmap mBitmapApple;

    // Set up the apple in the constructor
    Apple(Context context, Point mSpawnRange, int mSize) {
        // Make a note of the passed-in spawn range
        this.mSpawnRange = mSpawnRange;
        // Make a note of the size of an apple
        this.mSize = mSize;
        // Hide the apple off-screen until the game starts
        location.x = -10;

        // Load the image to the bitmap
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        // Resize the bitmap
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, mSize, mSize, false);
        random = new Random();
    }

    // This is called every time an apple is eaten
    void spawn() {
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    Point getLocation() {
        return location;
    }

    void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapApple, location.x * mSize, location.y * mSize, paint);
    }
}
