package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;

class Apple implements updateView {

    private final Point location = new Point();
    private final Point mSpawnRange;
    private final AppleType type;
    private final int mSize;
    private final Bitmap mBitmapApple;
    private final Random random;

    private Apple(Point spawnRange, int size, Bitmap bitmap , AppleType type) {
        this.mSpawnRange = spawnRange;
        this.mSize = size;
        this.mBitmapApple = bitmap;
        this.type = type;
        this.random = new Random();
        this.location.x = -10; // Hide the apple off-screen until the game starts
    }

    Point getLocation() {
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapApple, location.x * mSize, location.y * mSize, paint);
    }

    @Override
    public void move() {
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    static class AppleBuilder {
        private final Context context;
        private Point spawnRange = new Point();
        private int size;
        private Bitmap bitmap;
        private AppleType type = AppleType.GOOD;

        AppleBuilder(Context context) {
            this.context = context;
        }

        AppleBuilder setSpawnRange(Point spawnRange) {
            this.spawnRange = spawnRange;
            return this;
        }

        AppleBuilder setSize(int size) {
            this.size = size;
            return this;
        }

        AppleBuilder setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }
        AppleBuilder setType(AppleType appleType) {
            switch (appleType){
                case BAD:
                  this.type = appleType;
                  //Need to write condition as per requirement
                    break;
                case GOOD:
                    //Need to write condition as per requirement
                    this.type = appleType;
                    break;
            }
           return this;
        }
        Apple build() {
            // Use default bitmap if not provided
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
                bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            }

            return new Apple(spawnRange, size, bitmap,type);
        }
    }
}
