package com.example.fitzone.modeles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private String id;
    private String question;
    private List<String> options;
    private int correctOption;

    public Question(JSONObject json) throws JSONException {
        this.id = json.optString("id");
        this.question = json.optString("question");
        this.correctOption = json.optInt("correctOption");
        this.options = new ArrayList<>();

        JSONArray optionsJson = json.optJSONArray("options");
        if (optionsJson != null) {
            for (int i = 0; i < optionsJson.length(); i++) {
                this.options.add(optionsJson.getString(i));
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }
}
