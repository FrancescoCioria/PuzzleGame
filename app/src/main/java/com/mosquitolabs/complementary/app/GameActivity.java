package com.mosquitolabs.complementary.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class GameActivity extends ActionBarActivity {


    private final int FPS = 35;

    private int levelIndex = 0;
    //    private int packageIndex = 0;
    private TextView title;

    private TextView textViewMovesDone;
    private TextView textViewBestScore;

    private RatingBar stars;

    ArrayList<Integer> gameHistory = new ArrayList<Integer>();

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
        }
        try {
            levelIndex = getIntent().getExtras().getInt("levelIndex");
//            packageIndex = getIntent().getExtras().getInt("packageIndex");
        } catch (Exception e) {
            levelIndex = 0;
//            packageIndex = 0;
        }


        gameView = (GameView) findViewById(R.id.gameView);
        stars = (RatingBar) findViewById(R.id.ratingBar);
        title = (TextView) findViewById(R.id.textViewCurrentLevel);

        findViewById(R.id.buttonReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        findViewById(R.id.buttonNextLevel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextLevel();
            }
        });

        findViewById(R.id.buttonPreviousLevel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousLevel();
            }
        });

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        textViewBestScore = (TextView) findViewById(R.id.textViewBestScore);
        textViewMovesDone = (TextView) findViewById(R.id.textViewMovesDone);

        AudioPlayer.getIstance().initSounds(this);
//        initLevel();

        gameView.getLayoutParams().width = Utility.getWidth(this);
        gameView.getLayoutParams().height = Utility.getWidth(this) * 80 / 100;

        gameView.init();

        updateMoves();
        updateViews();
        startLoop();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameView.refresh();
                handler.postDelayed(this, 1000 / FPS);
            }
        }, 1000 / FPS);
    }


    private void resetGame() {
        gameHistory.clear();
        gameView.resetGame();
        updateMoves();
    }

    private void startTransition(int direction) {
        gameHistory.clear();
        gameView.startTransition(direction);
        updateMoves();
    }

    public void nextLevel() {
        if (levelIndex < LevelCollection.getInstance().getNumberOfLevelsAvailable() - 1) {
            levelIndex++;
            startTransition(Utility.NEXT);
            updateViews();
        }
    }

    public void previousLevel() {
        if (levelIndex > 0) {
            levelIndex--;
            startTransition(Utility.PREVIOUS);
            updateViews();
        }
    }

    private void updateViews() {
        title.setText("Level " + Integer.toString(levelIndex + 1));
        String score = getCurrentLevelData().getStringScore().equals("null") ? "-" : getCurrentLevelData().getStringScore();
        textViewBestScore.setText("Best score: " + score);
    }

    private Double getCurrentScore() {
        int k = 2;
        Double score = 10 * Math.exp(-1.0 * (gameHistory.size() - getSolution().size()) / (getSolution().size() * k));
        return Utility.round(score, 1);
    }

    public void win() {
        updateScore();
        updateViews();
        winPopUp();
    }

    private void updateScore() {
        if (getCurrentLevelData().getStringScore().equals("null") || getCurrentScore() > Double.parseDouble(getCurrentLevelData().getStringScore())) {
            getCurrentLevelData().setScore(getCurrentScore());
            LevelCollection.getInstance().modifyLevelInSavedData(getCurrentLevelData());
        }

        Utility.shortToast(getCurrentLevelData().getStringScore(), this);
    }

    private void winPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_win,
                (ViewGroup) findViewById(R.id.layoutRoot));

        builder.setCancelable(false);
        builder.setView(layout);

        Button buttonNext = (Button) layout.findViewById(R.id.buttonNext);
        Button buttonPrevious = (Button) layout.findViewById(R.id.buttonPrevious);
        Button buttonMenu = (Button) layout.findViewById(R.id.buttonMenu);
        TextView textViewScore = (TextView) layout.findViewById(R.id.textViewScore);

        textViewScore.setText("Score: " + getCurrentLevelData().getStringScore());


        final AlertDialog alertDialog = builder.create();


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextLevel();
                alertDialog.dismiss();
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousLevel();
                alertDialog.dismiss();
            }
        });

        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }


    public void updateMoves() {
//        UPDATE MOVES
        textViewMovesDone.setText("" + gameHistory.size() + "/" + getSolution().size());

//        UPDATE STARS

//        stars.setRating(getCurrentStarsFloat());
    }

//    private float getCurrentStarsFloat() {
//        stars.setStepSize(1f / getSolution().size());
//        int gap = gameHistory.size() - getSolution().size();
//        return gap > 0 ? 2 - 0.25f * gap : 3 - (1f * gameHistory.size() / getSolution().size());
//    }

    private int getCurrentStars() {
        int gap = gameHistory.size() - getSolution().size();
        int stars = 3;
        if (gap <= 0) {

        } else if (gap <= 4) {
            stars = 2;
        } else if (gap <= 8) {
            stars = 1;
        } else {
            stars = 0;
        }

        return stars;
    }

    public LevelData getCurrentLevelData() {
        return LevelCollection.getInstance().getLevel(levelIndex);
    }

    private ArrayList<Integer> getSolution() {
        return getCurrentLevelData().getSolution();
    }

    public ArrayList<Integer> getGameHistory() {
        return gameHistory;
    }

    public int getFPS() {
        return FPS;
    }

}
