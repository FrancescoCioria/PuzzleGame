package com.mosquitolabs.complementary.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View {

    private final static int RED = Color.rgb(190, 0, 0);
    private final static int GREEN = Color.rgb(0, 160, 0);
    private final static int BLUE = Color.rgb(11, 97, 164);
    private final static int ORANGE = Color.rgb(255, 146, 0);
    private final static int PURPLE = Color.rgb(159, 62, 213);
    private final static int YELLOW = Color.rgb(255, 200, 0);

    private final static int LEFT_MARGIN_BUTTONS = 180;

    private final static int GAME_OVER = 1;
    private final static int DING = 2;
    private final static int NO_SOUND = 0;
    private final static int ERROR = 3;
    private final static int SELECT = 4;
    private final static int BLOCK = 5;

    private final static int EMPTY = 0;
    private final static int COLORED = 1;
    private final static int ICE_BLOCK = 2;
    private final static int ICE_BLOCK_BROKEN = 3;
    private final static int INVISIBLE = 4;


    private final int TRANSITION_DURATION = 800; // millis

    private int gap = 5;
    private int marginX;
    private int marginY;
    private int size = 100;
    private float currentGap = 5;
    private int currentMarginX;
    private int currentMarginY;
    private float currentSize = 100;

    private boolean firstDraw = true;

    private GameActivity context;

    private boolean firstTouchValid = false;
    private boolean lastTouchValid = true;
    private boolean hasJustReset = false;
    private boolean isTransition = false;
    private boolean isGrowing = false;

    private int columns;
    private int rows;

    private int transitionDirection;


    private ArrayList<ComplementarySquare> complementarySquares = new ArrayList<ComplementarySquare>();
    private ArrayList<ComplementarySquare> squaresOldLevel = new ArrayList<ComplementarySquare>();

    public ArrayList<VerticalWall> verticalWalls = new ArrayList<VerticalWall>();
    public ArrayList<HorizontalWall> horizontalWalls = new ArrayList<HorizontalWall>();

    public ArrayList<Bridge> verticalBridges = new ArrayList<Bridge>();
    public ArrayList<Bridge> horizontalBridges = new ArrayList<Bridge>();

    private ArrayList<Integer> game = new ArrayList<Integer>();


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (GameActivity) context;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (firstDraw) {
            firstDraw = false;
            init();
        }

        updateTransition();


        for (ComplementarySquare complementarySquare : complementarySquares) {
            complementarySquare.draw(canvas);
        }

        for (ComplementarySquare oldSquare : squaresOldLevel) {
            oldSquare.draw(canvas);
        }

        for (HorizontalWall horizontalWall : horizontalWalls) {
            horizontalWall.draw(canvas);
        }

        for (VerticalWall verticalWall : verticalWalls) {
            verticalWall.draw(canvas);
        }

        drawBridges(canvas);


    }

    private void drawBridges(Canvas canvas) {

        try {
            int squareX = (int) complementarySquares.get(getCurrentPosition()).getFloatX();
            int squareY = (int) complementarySquares.get(getCurrentPosition()).getFloatY();


            int stroke = (gap / 5) * 2 > 2 ? (gap / 5) * 2 : 2;

            for (int i = 0; i < rows * columns; i++) {
                if (canMoveToIndex(i)) {
                    Rect rect = null;
                    if (i < getCurrentPosition()) {
                        if (i == getCurrentPosition() - 1) {
//                        LEFT
                            rect = new Rect(squareX - gap, squareY + size / 2 - stroke / 2, squareX, squareY + size / 2 + stroke / 2);
                        } else {
//                        UP
                            rect = new Rect(squareX + size / 2 - stroke / 2, squareY - gap, squareX + size / 2 + stroke / 2, squareY);
                        }
                    } else {
                        if (i == getCurrentPosition() + 1) {
//                        RIGHT
                            rect = new Rect(squareX + size, squareY + size / 2 - stroke / 2, squareX + size + gap, squareY + size / 2 + stroke / 2);
                        } else {
//                        DOWN
                            rect = new Rect(squareX + size / 2 - stroke / 2, squareY + size, squareX + size / 2 + stroke / 2, squareY + size + gap);

                        }
                    }
                    Paint gray = new Paint();
                    gray.setColor(Color.GRAY);

                    canvas.drawRect(rect, gray);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTransition) {
            return true;
        }

        if (hasJustReset && event.getAction() == MotionEvent.ACTION_DOWN) {
            hasJustReset = false;
        } else if (hasJustReset) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handleTouch(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            firstTouchValid = false;
            lastTouchValid = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && firstTouchValid) {
            handleTouch(event);
        }

        return true;
    }

    private void handleTouch(MotionEvent event) {
        int index = 0;
        for (ComplementarySquare complementarySquare : complementarySquares) {
            if (complementarySquare.isCollition(event.getX(), event.getY())) {
                if (index == getCurrentPosition()) {
                    firstTouchValid = true;
                    return;
                } else if (index == getLastPosition()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        firstTouchValid = true;
                    }
                    moveBack();
                    context.updateMoves();
                    return;
                } else {
                    if (tryMovingToIndex(index) && event.getAction() == MotionEvent.ACTION_DOWN) {
                        firstTouchValid = true;
                    }
                    context.updateMoves();
                    return;
                }
            }
            index++;
        }

    }


    public void refresh() {
        invalidate();
    }


    public void init() {
        initGame();
        initColors();
        initializeGrid(getComplementaryLevelData().getColumns(), getComplementaryLevelData().getRows());
        resetCurrentVariables();
        initComplementarySquaresAndWalls();
    }

    private void initGame() {
        game.clear();
        verticalWalls.clear();
        horizontalWalls.clear();


        for (String row : getComplementaryLevelData().getGameRows()) {
            for (int i = 0; i < row.length(); i++) {
                String element = String.valueOf(row.charAt(i));
                if (!element.equals("|") && !element.equals("‾") && !element.equals("T")) {
                    game.add(Integer.parseInt(element));
                } else if (element.equals("|")) {
                    VerticalWall verticalWall = new VerticalWall();
                    verticalWall.initSquaresOnSides(game.size() - 1, game.size());
                    verticalWalls.add(verticalWall);
                } else if (element.equals("‾")) {
                    HorizontalWall horizontalWall = new HorizontalWall();
                    horizontalWall.initSquaresOnSides(game.size() - 1, game.size() - 1 - getComplementaryLevelData().getColumns());
                    horizontalWalls.add(horizontalWall);
                } else if (element.equals("T")) {
                    VerticalWall verticalWall = new VerticalWall();
                    verticalWall.initSquaresOnSides(game.size() - 1, game.size());
                    verticalWalls.add(verticalWall);
                    HorizontalWall horizontalWall = new HorizontalWall();
                    horizontalWall.initSquaresOnSides(game.size() - 1, game.size() - 1 - getComplementaryLevelData().getColumns());
                    horizontalWalls.add(horizontalWall);
                }
            }
        }
    }

    private void initColors() {

    }


    public void initializeGrid(int columns, int rows) {

        this.columns = columns;
        this.rows = rows;

        float sizePlusGap = getWidth() * columns < getHeight() * rows ? getWidth() / (float) columns : getHeight() / (float) rows;

        size = (int) (sizePlusGap * 9 / 10);
        gap = (int) (sizePlusGap / 10);


        int gridWidth = size * columns + gap * (columns - 1);
        int gridHeight = size * rows + gap * (rows - 1);

        marginX = gridWidth < getWidth() ? (getWidth() - gridWidth) / 2 : 0;
        marginY = gridHeight < getHeight() ? (getHeight() - gridHeight) / 2 : 0;

    }

    private void resetCurrentVariables() {
        currentGap = gap;
        currentMarginX = marginX;
        currentMarginY = marginY;
        currentSize = size;
    }


    public void startTransition(int direction) {
        isTransition = true;
        transitionDirection = direction;

        copySquares();

        initGame();
        initializeGrid(getComplementaryLevelData().getColumns(), getComplementaryLevelData().getRows());
        resetSquaresStatus();


        switch (transitionDirection) {
            case Utility.NEXT:
                for (ComplementarySquare complementarySquare : complementarySquares) {
                    complementarySquare.setNewSizeAndCoordinates(size, size, complementarySquare.getFloatX() + Utility.getWidth(context), complementarySquare.getFloatY());
                }

                break;
            case Utility.PREVIOUS:
                for (ComplementarySquare complementarySquare : complementarySquares) {
                    complementarySquare.setNewSizeAndCoordinates(size, size, complementarySquare.getFloatX() - Utility.getWidth(context), complementarySquare.getFloatY());
                }
                break;
        }


    }


    private void updateTransition() {
        if (isTransition) {
            float step = 1.0f * Utility.getWidth(context) / (TRANSITION_DURATION / context.getFPS());
            step = transitionDirection == Utility.NEXT ? -step : step;

            for (int i = 0; i < complementarySquares.size(); i++) {
                ComplementarySquare oldSquare = squaresOldLevel.get(i);
                ComplementarySquare newSquare = complementarySquares.get(i);
                oldSquare.setNewSizeAndCoordinates(size, size, oldSquare.getFloatX() + step, oldSquare.getFloatY());
                newSquare.setNewSizeAndCoordinates(size, size, newSquare.getFloatX() + step, newSquare.getFloatY());
            }

            if ((transitionDirection == Utility.NEXT && complementarySquares.get(0).getFloatX() <= marginX) || (transitionDirection == Utility.PREVIOUS && complementarySquares.get(0).getFloatX() >= marginX)) {
                applyTransformationToSquares();
                isTransition = false;
                squaresOldLevel.clear();
            }

        }

    }


    public void startTransitionFlip() {
        isTransition = true;
        isGrowing = false;
    }


    private void updateTransitionFlip() {
        if (isTransition) {

//            MODIFY SIZE AND GAP

            float dividerFactor = (TRANSITION_DURATION / context.getFPS()) / 2f;

            float sizeStep = size / dividerFactor;
            float gapStep = gap / dividerFactor;

            if (!isGrowing) {
                currentSize = currentSize <= 0 ? 0 : currentSize - sizeStep;
                currentGap = currentGap <= 0 ? 0 : currentGap - gapStep;
            } else {
                currentSize = currentSize >= size ? size : currentSize + sizeStep;
                currentGap = currentGap >= gap ? gap : currentGap + gapStep;
            }

//            VERIFY TRANSITION PROGRESS

            if (!isGrowing && currentSize == 0) {
                initGame();
                initializeGrid(getComplementaryLevelData().getColumns(), getComplementaryLevelData().getRows());
                resetSquaresStatus();
                currentSize = 0;
                currentGap = 0;
                isGrowing = true;
            } else if (isGrowing && (currentSize == size && currentGap == gap)) {
                isTransition = false;
                isGrowing = false;
            }

            int gridWidth = (int) (currentSize * columns + currentGap * (columns - 1));

            currentMarginX = gridWidth < getWidth() ? (getWidth() - gridWidth) / 2 : 0;

//            APPLY TRANSFORMATION

            applyTransformationToSquares();

        }

    }

    private void applyTransformationToSquares() {
        for (int row = 0; row < rows; row++) {
            int y = marginY + row * (size + gap);
            for (int column = 0; column < columns; column++) {
                int x = (int) (currentMarginX + column * (currentSize + currentGap));
                int index = row * columns + column;
                complementarySquares.get(index).setNewSizeAndCoordinates((int) currentSize, size, x, y);
            }
        }
    }

    private void initComplementarySquaresAndWalls() {
        complementarySquares.clear();

        final int columns = getComplementaryLevelData().getColumns();
        final int rows = getComplementaryLevelData().getRows();

        for (int row = 0; row < rows; row++) {
            int y = (int) (currentMarginY + row * (currentSize + currentGap));
            for (int column = 0; column < columns; column++) {
                int x = (int) (currentMarginX + column * (currentSize + currentGap));
                int index = row * columns + column;
                complementarySquares.add(new ComplementarySquare(this, x, y, currentSize, currentSize, index));
                for (VerticalWall verticalWall : verticalWalls) {
                    if (verticalWall.getSquareOnTheLeft() == index) {
                        verticalWall.initPositionAndSize(x + currentSize + (currentGap / 3), y, currentGap / 3, currentSize);
                    }
                }
                for (HorizontalWall horizontalWall : horizontalWalls) {
                    if (horizontalWall.getSquareBelow() == index) {
                        horizontalWall.initPositionAndSize(x, y - (currentGap * 2 / 3), currentSize, currentGap / 3);
                    }
                }
            }
        }
    }


    private void copySquares() {
        squaresOldLevel.clear();

        final int columns = getComplementaryLevelData().getColumns();
        final int rows = getComplementaryLevelData().getRows();

        for (int row = 0; row < rows; row++) {
            int y = (int) (currentMarginY + row * (currentSize + currentGap));
            for (int column = 0; column < columns; column++) {
                int x = (int) (currentMarginX + column * (currentSize + currentGap));
                int index = row * columns + column;
                ComplementarySquare oldSquare = new ComplementarySquare(this, x, y, currentSize, currentSize, index);
                if (oldSquare.getCurrentState() != complementarySquares.get(index).getCurrentState()) {
                    oldSquare.invertStatus();
                }
                squaresOldLevel.add(oldSquare);
            }
        }
    }


    private void moveBack() {
        complementarySquares.get(getCurrentPosition()).invertStatus();
        context.getGameHistory().remove(context.getGameHistory().size() - 1);
        lastTouchValid = true;
        AudioPlayer.getIstance().playDeselect();
    }

    private boolean tryMovingToIndex(int index) {
        if (canMoveToIndex(index)) {
            context.getGameHistory().add(index);
            complementarySquares.get(index).invertStatus();
            AudioPlayer.getIstance().playSelect();
            checkWin();
            lastTouchValid = true;
            return true;
        }

        if (lastTouchValid) {
            AudioPlayer.getIstance().playWrongMove();
        }
        lastTouchValid = false;
        return false;
    }

    private boolean canMoveToIndex(int index) {
        if (getCurrentPosition() == -1) {
            return true;
        }

        int columns = getComplementaryLevelData().getColumns();

        int currentRow = getCurrentPosition() / columns;
        int currentColumn = getCurrentPosition() % columns;

        int nextRow = index / columns;
        int nextColumn = index % columns;

        if (((nextRow == currentRow + 1 || nextRow == currentRow - 1) && (nextColumn == currentColumn)) || ((nextColumn == currentColumn + 1 || nextColumn == currentColumn - 1) && (nextRow == currentRow))) {

            if (isWallBetween(getCurrentPosition(), index)) {
                return false;
            } else if (complementarySquares.get(index).isNormal()) {
                return true;
            } else if (complementarySquares.get(index).isIceBlockNotBroken()) {
                return true;
            }
        }

        return false;

    }

    public boolean isWallBetween(int first, int second) {
        if (first == second) {
            return false;
        }

        if (first == second + 1 || first == second - 1) {
            for (VerticalWall verticalWall : verticalWalls) {
                if ((verticalWall.getSquareOnTheLeft() == first && verticalWall.getSquareOnTheRight() == second) || (verticalWall.getSquareOnTheLeft() == second && verticalWall.getSquareOnTheRight() == first)) {
                    return true;
                }
            }
        }

        if (first == second + getComplementaryLevelData().getColumns() || first == second - getComplementaryLevelData().getColumns()) {
            for (HorizontalWall horizontalWall : horizontalWalls) {
                if ((horizontalWall.getSquareBelow() == first && horizontalWall.getSquareAbove() == second) || (horizontalWall.getSquareBelow() == second && horizontalWall.getSquareAbove() == first)) {
                    return true;
                }
            }
        }

        return false;

    }

    private void checkWin() {
        for (ComplementarySquare complementarySquare : complementarySquares) {
            if (complementarySquare.getCurrentState() != EMPTY) {
                return;
            }
        }

        // YOU WON!!!
        AudioPlayer.getIstance().playWin();
        context.win();
    }

    public int getCurrentPosition() {
        if (context.getGameHistory().size() > 0) {
            return context.getGameHistory().get(context.getGameHistory().size() - 1);
        } else {
            return -1;
        }
    }

    public int getLastPosition() {
        if (context.getGameHistory().size() > 1) {
            return context.getGameHistory().get(context.getGameHistory().size() - 2);
        } else {
            return -1;
        }
    }


    public LevelData getComplementaryLevelData() {
        return context.getCurrentLevelData();
    }

    public ArrayList<Integer> getGame() {
        return game;
    }

    public int getSetOfColors() {
        return 0;
    }


    private void resetSquaresStatus() {
        for (ComplementarySquare complementarySquare : complementarySquares) {
            complementarySquare.resetStatus();
        }
    }

    public void resetGame() {
        init();
        resetSquaresStatus();
        hasJustReset = true;
    }


}
