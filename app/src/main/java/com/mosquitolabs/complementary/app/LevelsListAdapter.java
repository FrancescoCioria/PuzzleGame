package com.mosquitolabs.complementary.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by francesco on 7/28/14.
 */
public class LevelsListAdapter extends BaseAdapter {
    private final int PACKAGE_LEVELS = 30;

    private Activity context;
    private LayoutInflater inflater;
    private int packageIndex;

    private final int numberOfItems = 5;


    public LevelsListAdapter(Activity paramContext, int packageIndex) {
        this.inflater = LayoutInflater.from(paramContext);
        this.context = paramContext;
        this.packageIndex = packageIndex;
    }

    public int getCount() {
        return PACKAGE_LEVELS / numberOfItems;
    }

    public Object getItem(int paramInt) {
        return paramInt;
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }


    private void startGameActivity(int levelIndex) {
        Intent mIntent = new Intent(context, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("levelIndex", levelIndex);
//        bundle.putInt("packageIndex", packageIndex);
        mIntent.putExtras(bundle);
        context.startActivity(mIntent);
    }


    public View getView(final int paramInt, View paramView,
                        ViewGroup paramViewGroup) {

        final int firstItemIndex = paramInt * numberOfItems + packageIndex * PACKAGE_LEVELS;


        if (firstItemIndex < LevelCollection.getInstance().getNumberOfLevelsAvailable()) {
            UnlockedLevelItemViewHolder unlockedLevelItemViewHolder;


            try {
                unlockedLevelItemViewHolder = (UnlockedLevelItemViewHolder) paramView.getTag();
            } catch (Exception e) {
                paramView = inflater.inflate(R.layout.level_list_item5, null);
                unlockedLevelItemViewHolder = new UnlockedLevelItemViewHolder();

                unlockedLevelItemViewHolder.background = (LinearLayout) paramView.findViewById(R.id.listItemLayout);

                for (int i = 0; i < numberOfItems; i++) {
                    int resTextViewIndex = context.getResources().getIdentifier("textViewIndex" + Integer.toString(i + 1), "id", context.getPackageName());
                    int resTextViewScore = context.getResources().getIdentifier("textViewScore" + Integer.toString(i + 1), "id", context.getPackageName());
                    int resLayout = context.getResources().getIdentifier("level" + Integer.toString(i + 1), "id", context.getPackageName());

                    unlockedLevelItemViewHolder.textViewIndexes[i] = (TextView) paramView.findViewById(resTextViewIndex);
                    unlockedLevelItemViewHolder.textViewScores[i] = (TextView) paramView.findViewById(resTextViewScore);
                    unlockedLevelItemViewHolder.layouts[i] = (RelativeLayout) paramView.findViewById(resLayout);
                }

                paramView.setTag(unlockedLevelItemViewHolder);
            }


            for (int i = 0; i < numberOfItems; i++) {
                final int counter = i;
                unlockedLevelItemViewHolder.textViewIndexes[i].setText(Integer.toString(firstItemIndex + i + 1));
                String score = LevelCollection.getInstance().getLevel(firstItemIndex + i).getStringScore().equals("null") ? "" : LevelCollection.getInstance().getLevel(firstItemIndex + i).getStringScore();
                unlockedLevelItemViewHolder.textViewScores[i].setText(score);


                unlockedLevelItemViewHolder.layouts[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startGameActivity(firstItemIndex + counter);
                    }
                });
            }

//        int resBackground;
//
//        if (paramInt == 0) {
//            resBackground = context.getResources().getIdentifier("rounded_layout_top", "drawable", context.getPackageName());
//        } else if (paramInt == getCount() - 1) {
//            resBackground = context.getResources().getIdentifier("rounded_layout_bottom", "drawable", context.getPackageName());
//        } else {
//            resBackground = context.getResources().getIdentifier("rounded_layout_center", "drawable", context.getPackageName());
//        }
//        levelItemViewHolder.background.setBackground(context.getResources().getDrawable(resBackground));

            return paramView;

        } else {

            LockedLevelItemViewHolder lockedLevelItemViewHolder;

            try {
                lockedLevelItemViewHolder = (LockedLevelItemViewHolder) paramView.getTag();
            } catch (Exception e) {
                paramView = inflater.inflate(R.layout.locked_level_list_item5, null);
                lockedLevelItemViewHolder = new LockedLevelItemViewHolder();

                lockedLevelItemViewHolder.background = (LinearLayout) paramView.findViewById(R.id.listItemLayout);
                lockedLevelItemViewHolder.textViewNeededPoints = (TextView) paramView.findViewById(R.id.textViewNeededPoints);


                for (int i = 0; i < numberOfItems; i++) {
                    int resTextViewIndex = context.getResources().getIdentifier("textViewIndex" + Integer.toString(i + 1), "id", context.getPackageName());
                    int resLayout = context.getResources().getIdentifier("level" + Integer.toString(i + 1), "id", context.getPackageName());

                    lockedLevelItemViewHolder.textViewIndexes[i] = (TextView) paramView.findViewById(resTextViewIndex);
                    lockedLevelItemViewHolder.layouts[i] = (RelativeLayout) paramView.findViewById(resLayout);
                }
                paramView.setTag(lockedLevelItemViewHolder);
            }


            for (int i = 0; i < numberOfItems; i++) {
                lockedLevelItemViewHolder.textViewIndexes[i].setText("X");
            }
            if (firstItemIndex % 10 == 0) {
                lockedLevelItemViewHolder.textViewNeededPoints.setVisibility(View.VISIBLE);
                lockedLevelItemViewHolder.textViewNeededPoints.setText("You need "+LevelCollection.getInstance().getNeededPointsForLevel(firstItemIndex)+" points");
            } else {
                lockedLevelItemViewHolder.textViewNeededPoints.setVisibility(View.GONE);
            }

            return paramView;

        }

    }


    static class UnlockedLevelItemViewHolder {
        TextView[] textViewIndexes = {null, null, null, null, null};
        TextView[] textViewScores = {null, null, null, null, null};
        RelativeLayout[] layouts = {null, null, null, null, null};
        LinearLayout background;
    }

    static class LockedLevelItemViewHolder {
        TextView[] textViewIndexes = {null, null, null, null, null};
        RelativeLayout[] layouts = {null, null, null, null, null};
        TextView textViewNeededPoints;
        LinearLayout background;
    }

}
