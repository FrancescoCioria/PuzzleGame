package com.mosquitolabs.complementary.app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bridge {

    private int corners;

    private int squareAbove;
    private int squareBelow;

    private Paint paint = new Paint();
    private Rect rect;

    private boolean isVisible = false;


    public Bridge() {
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setAlpha(180);
    }

    public void initPositionAndSize(float x, float y, int width, int height) {
        rect = new Rect((int) x, (int) y, (int) x + width, (int) y + height);
    }

    public void initPositionAndSize(float x, float y, float width, float height) {
        rect = new Rect((int) x, (int) y, (int) x + (int) width, (int) y + (int) height);
    }

    public void draw(Canvas canvas) {
        if (isVisible) {
            canvas.drawRect(rect, paint);
        }
    }

    public void setVisible(boolean b) {
        isVisible = b;
    }

}
