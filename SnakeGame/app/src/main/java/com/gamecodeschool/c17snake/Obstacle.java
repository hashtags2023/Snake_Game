package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

public class Obstacle {

    private Point location;
    private Paint obstaclePaint;
    private int size;

    public Obstacle(Point location, int size) {
        this.location = location;
        this.size = size;

        // Defining paint properties for drawing obstacle
        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.RED);
        obstaclePaint.setStyle(Paint.Style.FILL);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(
                location.x * size,
                location.y * size,
                (location.x * size) + size,
                (location.y * size) + size,
                obstaclePaint
        );
    }

    public Point getLocation() {
        return location;
    }

    public int getSize() {
        return size;
    }

    public void setLocation(Point newLocation) {
        location = newLocation;
    }

    public void setSize(int newSize) {
        size = newSize;
    }

}