package com.mosquitolabs.complementary.app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;

public class ComplementarySquare {


    private Paint frame = new Paint();


    private int x;
    private float floatX;
    private float floatY;
    private int y;
    private int width;
    private int height;
    private int squareIndex;
    private int counter = 0;
    // private int primaryColor = 0;
    // private int complementaryColor = 0;
    private int currentState;


    private final static int YELLOW_PURPLE = 0;
    private final static int BLUE_ORANGE = 1;
    private final static int GREEN_RED = 2;
    private final static int YELLOW_ORANGE_RED = 3;
    private final static int BLUE_ORANGE_GREEN = 4;
    private final static int GREEN_RED_YELLOW = 5;

    final static int EMPTY = 0;
    final static int COLORED = 1;
    final static int ICE_BLOCK = 2;
    final static int ICE_BLOCK_BROKEN = 3;
    final static int INVISIBLE = 4;

    final static int EMPTY_STAR = 0;
    final static int FULL_STAR = 1;

    private final static int RED = Color.rgb(190, 0, 0);
    private final static int GREEN = Color.rgb(0, 160, 0);
    private final static int BLUE = Color.rgb(11, 97, 164);
    private final static int ORANGE = Color.rgb(255, 146, 0);
    private final static int PURPLE = Color.rgb(159, 62, 213);
    private final static int YELLOW = Color.rgb(255, 200, 0);
    private static int FRAME_COLOR = Color.WHITE;

    private boolean isStar = false;
    private boolean hasTakenStar = false;

    private GameView gameView;

    private RectF roundedRect;
    private RectF currentRoundedSignalRect;
    private RectF lastRoundedSignalRect;

    private Paint rectPaintColored = new Paint();
    private Paint rectPaintEmpty = new Paint();


    private ArrayList<Integer> colors = new ArrayList<Integer>();

    public ComplementarySquare(GameView gameView, float xp, float yp, int width, int height, int squareIndex) {
        this.gameView = gameView;
        this.width = width;
        this.height = height;
        this.squareIndex = squareIndex;
        x = (int) xp;
        y = (int) yp;

        init();
    }

    public ComplementarySquare(GameView gameView, float xp, float yp, float width, float height, int squareIndex) {
        floatX = xp;
        floatY = yp;
        this.gameView = gameView;
        this.width = (int) width;
        this.height = (int) height;
        this.squareIndex = squareIndex;
        x = (int) xp;
        y = (int) yp;

        init();
    }


    private void init() {
        roundedRect = new RectF(new Rect(x, y, x + width, y + height));
        currentRoundedSignalRect = new RectF(new Rect(x + width / 3, y + height / 3, x + width * 2 / 3, y + height * 2 / 3));
        lastRoundedSignalRect = new RectF(new Rect(x + width * 3 / 8, y + height * 3 / 8, x + width * 5 / 8, y + height * 5 / 8));
        rectPaintColored.setAntiAlias(true);
        rectPaintEmpty.setAntiAlias(true);

        rectPaintColored.setColor(BLUE);
        rectPaintEmpty.setColor(Color.WHITE);
        rectPaintEmpty.setAlpha(80);

        currentState = gameView.getGame().get(squareIndex);

        colors.clear();
    }

    public void draw(Canvas canvas) {
        if (currentState != INVISIBLE) {

            drawSquare(canvas);
            drawSignalSquare(canvas);

        }
    }

    private void drawSquare(Canvas canvas) {
        switch (currentState) {
            case EMPTY:
                canvas.drawRoundRect(roundedRect, width / 15, height / 15, rectPaintEmpty);
                break;
            case COLORED:
                canvas.drawRoundRect(roundedRect, width / 15, height / 15, rectPaintColored);
                break;
        }
    }

    private void drawSignalSquare(Canvas canvas) {
        if (gameView.getCurrentPosition() == squareIndex) {
//            canvas.drawRoundRect(currentRoundedSignalRect, width / 15, height / 15, rectPaintEmpty);

        } else if (gameView.getLastPosition() == squareIndex) {
//            canvas.drawRoundRect(lastRoundedSignalRect, width / 15, height / 15, rectPaintEmpty);
            Paint gray = new Paint();
            gray.setColor(Color.GRAY);
            canvas.drawRoundRect(roundedRect, width / 15, height / 15, gray);
        }

    }

    public void setNewSizeAndCoordinates(int width, int height, float xp, float yp) {
        floatX = xp;
        floatY = yp;
        x = (int) xp;
        y = (int) yp;
        this.width = width;
        this.height = height;

        roundedRect = new RectF(new Rect(x, y, x + width, y + height));
        currentRoundedSignalRect = new RectF(new Rect(x + width / 3, y + height / 3, x + width * 2 / 3, y + height * 2 / 3));
        lastRoundedSignalRect = new RectF(new Rect(x + width * 3 / 8, y + height * 3 / 8, x + width * 5 / 8, y + height * 5 / 8));

//        currentState = gameView.getGame().get(squareIndex);

    }


    public boolean isCollition(float x2, float y2) {
        return currentState != INVISIBLE && x2 > x && x2 < x + width && y2 > y && y2 < y + height;
    }


    public void setHasTakenStar(boolean b) {
        hasTakenStar = b;
    }


    public boolean hasTakenStar() {
        if (isStar) {
// FARE CICLO E VEDERE SE GAME HISTORY CONTIENE IL MIO INDEX.
        }
        return false;
    }

    public void invertStatus() {
        if (isNormal()) {
            currentState = currentState == EMPTY ? COLORED : EMPTY;

        } else if (isIceBlock()) {
            currentState = currentState == ICE_BLOCK ? ICE_BLOCK_BROKEN : ICE_BLOCK;
        }

    }

    public void resetStatus() {
        currentState = gameView.getGame().get(squareIndex);
    }


    public int getCurrentState() {
        return currentState;
    }

    public float getFloatX() {
        return floatX;
    }

    public float getFloatY() {
        return floatY;
    }

    public boolean isNormal() {
        if (currentState == EMPTY || currentState == COLORED) {
            return true;
        }
        return false;
    }

    public boolean isIceBlock() {
        return (currentState == ICE_BLOCK || currentState == ICE_BLOCK_BROKEN);
    }

    public boolean isIceBlockNotBroken() {
        return currentState == ICE_BLOCK;
    }


}
