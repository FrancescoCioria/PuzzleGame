package com.mosquitolabs.complementary.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by francesco on 7/29/14.
 */
public class LevelCollection {

    private JSONArray savedData = null;

    //    private ArrayList<JSONArray> jsonArrayPackages = new ArrayList<JSONArray>();
    private JSONArray jsonArrayLevels;

    private ArrayList<LevelData> levels = new ArrayList<LevelData>();

    private static LevelCollection instance = new LevelCollection();

//    private String[] jsonPackagesId = {"3x3"};

    private Context context;


    public static LevelCollection getInstance() {
        return instance;
    }


    public void initCollection(final Context context) {
        this.context = context;
        try {
            jsonArrayLevels = new JSONArray(getJsonFromRaw("levels3x3"));

            initSavedData();
            populateCollection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSavedData() {
        try {
            String saved_data = Utility.getSharedPreferences().getString("saved_data", "null");

            if (saved_data.equals("null")) {
                savedData = new JSONArray(getJsonFromRaw("saved_data"));
            } else {
                savedData = new JSONArray(saved_data);
            }

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public void populateCollection() {
        for (int i = 0; i < jsonArrayLevels.length(); i++) {
            LevelData levelData = new LevelData();
            try {
                levelData.initLevel(jsonArrayLevels.getJSONObject(i));
                levelData.setId(Integer.toString(i));
                levelData.populateLevelWithSavedData(savedData);
                levels.add(levelData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public LevelData getLevel(int levelIndex) {
        return levels.get(levelIndex);
    }

    public Double getTotalPoints() {
        double points = 0;
        for (int i = 0; i < getNumberOfLevels(); i++) {
            points += getLevel(i).getStringScore().equals("null") ? 0 : Double.parseDouble(getLevel(i).getStringScore());
        }
        return Utility.round(points, 1);
    }

    public int getNumberOfLevelsAvailable() {
        return (getTotalPoints().intValue() / 70 + 1) * 10;
    }

    public int getNumberOfLevels() {
        return levels.size();
    }

    public int getNeededPointsForLevel(int levelIndex) {
        if (levelIndex < getNumberOfLevelsAvailable()) {
            return 0;
        }

        return getNumberOfLevelsAvailable() * 7 + (levelIndex - getNumberOfLevelsAvailable()) / 10 * 70;
    }


    private String getJsonFromRaw(String jsonPath) {
        String JSONString;
        try {
            //open the inputStream to the file
            int res = context.getResources().getIdentifier(jsonPath, "raw", context.getPackageName());
            InputStream inputStream = context.getResources().openRawResource(res);

            int sizeOfJSONFile = inputStream.available();

            //array that will store all the data
            byte[] bytes = new byte[sizeOfJSONFile];

            //reading data into the array from the file
            inputStream.read(bytes);

            //close the input stream
            inputStream.close();

            JSONString = new String(bytes, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return JSONString;

    }


    public void modifyLevelInSavedData(LevelData levelData) {
        try {
            if (savedData == null) {
                initSavedData();
            }
            for (int i = 0; i < savedData.length(); i++) {
                if (savedData.getJSONObject(i).getString("id").equals(levelData.getId())) {
                    savedData.put(i, new JSONObject().put("id", levelData.getId()).put("score", levelData.getStringScore()).put("used_hints", Integer.toString(levelData.getUsedHints())));

                    SharedPreferences.Editor editor = Utility.getSharedPreferences().edit();
                    editor.putString("saved_data", savedData.toString());
                    editor.commit();
                    return;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
