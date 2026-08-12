package com.example.fitzone.modeles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private String id;
    private String programId;
    private String title;
    private List<Question> questions;

    public Quiz(JSONObject json) throws JSONException {
        this.id = json.optString("id");
        this.programId = json.optString("programId");
        this.title = json.optString("title");
        this.questions = new ArrayList<>();

        JSONArray questionsJson = json.optJSONArray("questions");
        if (questionsJson != null) {
            for (int i = 0; i < questionsJson.length(); i++) {
                this.questions.add(new Question(questionsJson.getJSONObject(i)));
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getProgramId() {
        return programId;
    }

    public String getTitle() {
        return title;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
