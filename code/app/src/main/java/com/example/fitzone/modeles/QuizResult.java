package com.example.fitzone.modeles;

import org.json.JSONException;
import org.json.JSONObject;

public class QuizResult {

    private String quizId;
    private int score;
    private int total;

    public QuizResult(String quizId, int score, int total) {
        this.quizId = quizId;
        this.score = score;
        this.total = total;
    }

    public QuizResult(JSONObject json) throws JSONException {
        this.quizId = json.getString("quizId");
        this.score = json.getInt("score");
        this.total = json.getInt("total");
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("quizId", quizId);
        json.put("score", score);
        json.put("total", total);
        return json;
    }

    public String getQuizId() {
        return quizId;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }
}
