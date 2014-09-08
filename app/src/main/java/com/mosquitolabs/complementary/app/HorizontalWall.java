package com.mosquitolabs.complementary.app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class HorizontalWall {

    private int corners;

    private int squareAbove;
    private int squareBelow;

    private Paint paint = new Paint();
    private RectF roundedRect;


    public HorizontalWall() {
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setAlpha(180);
    }

    public void initPositionAndSize(float x, float y, int width, int height) {
        corners = Math.min(width, height) / 2;
        roundedRect = new RectF(new Rect((int) x, (int) y, (int) x + width, (int) y + height));
    }

    public void initPositionAndSize(float x, float y, float width, float height) {
        corners = Math.min((int) width, (int) height) / 2;
        roundedRect = new RectF(new Rect((int) x, (int) y, (int) x + (int) width, (int) y + (int) height));
    }

    public void initSquaresOnSides(int squareBelow, int squareAbove) {
        this.squareAbove = squareAbove;
        this.squareBelow = squareBelow;
    }

    public void draw(Canvas canvas) {
        canvas.drawRoundRect(roundedRect, corners, corners, paint);
    }

    public int getSquareAbove() {
        return squareAbove;
    }

    public int getSquareBelow() {
        return squareBelow;
    }

}
