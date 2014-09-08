package com.mosquitolabs.complementary.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class LevelData implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<String> gameRows = new ArrayList<String>();
    private ArrayList<Integer> solution = new ArrayList<Integer>();

    private int rows = 3;
    private int columns = 3;

    private String score = "null";
    private int usedHints = 0;
    private String id = null;


    public void initLevel(JSONObject jsonLevel) {
        gameRows.clear();

        try {
//            JSONObject main = new JSONObject(jsonLevel);
            JSONArray rows = jsonLevel.getJSONArray("rows");

            for (int rowIndex = 0; rowIndex < rows.length(); rowIndex++) {
                String row = rows.getString(rowIndex);
                gameRows.add(row);
            }

            columns = this.gameRows.get(0).replace("|", "").replace("_", "").length();
            this.rows = rows.length();

            solution.clear();
            for (String squareIndex : jsonLevel.getString("solution").split(",")) {
                solution.add(Integer.parseInt(squareIndex));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void populateLevelWithSavedData(JSONArray savedData) {
        try {
            for (int i = 0; i < savedData.length(); i++) {
                if (savedData.getJSONObject(i).getString("id").equals(getId())) {
                    JSONObject savedLevel = savedData.getJSONObject(i);

                    setScore(savedLevel.getString("score"));
                    setUsedHints(Integer.parseInt(savedLevel.getString("used_hints")));

                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getGameRows() {
        return gameRows;
    }

    public ArrayList<Integer> getSolution() {
        return solution;
    }


    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getUsedHints() {
        return usedHints;
    }

    public void setUsedHints(int usedHints) {
        this.usedHints = usedHints;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getStringScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setScore(Double score) {
        this.score = Double.toString(score);
    }

}
