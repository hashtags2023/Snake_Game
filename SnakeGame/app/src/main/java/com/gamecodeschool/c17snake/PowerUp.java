package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;

import java.util.Random;

public class PowerUp {
    public enum PowerType {
        SPEED_UP,
        INVULNERABILITY,
        // Add more power-up types as needed
    }
    private final Point location = new Point();
    private final Point mSpawnRange;
    public PowerType type;
    private final Random random;
    private Integer mSize = 50 ;
    private Bitmap speedBitmap;

    public boolean isSnakeHasPower = false ;

    public boolean isPowerUpShowing  = false;


    PowerUp(Point spawnRange , Context context , int size){
        this.random = new Random();
        this.mSpawnRange = spawnRange;
        this.mSize =size;
        speedBitmap  = BitmapFactory.decodeResource(context.getResources(), R.drawable.speed_power);
        speedBitmap = Bitmap.createScaledBitmap(speedBitmap, mSize, mSize, false);
        move();
    }
    public void reset(){

        isSnakeHasPower = false ;
        isPowerUpShowing  = false;
    }
    Point getLocation() {
        return location;
    }


    public void move(){
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }
    public void draw(Canvas canvas, Paint paint ) {


            int speedItemType = random.nextInt(2);
            switch (speedItemType){
                case 0:
                    this.type  = PowerType.SPEED_UP ;
                    canvas.drawBitmap(speedBitmap, location.x * mSize, location.y * mSize, paint);
                    break;
                case 1:
                    this.type  = PowerType.INVULNERABILITY ;
                    canvas.drawBitmap(speedBitmap, location.x * mSize, location.y * mSize, paint);
                    break;
            }


    }
    public void hidePowerUp(){
        this.location.x = -10;
    }


    public void EnableToShowPowerUp() {
        isPowerUpShowing = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isPowerUpShowing = false;
            }
        }, 5000);
    }

    public void setSnakeHasPower(){
        isPowerUpShowing = false;
        isSnakeHasPower = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isSnakeHasPower = false;
            }
        }, 5000);
    }
}
