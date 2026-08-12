package com.example.fitzone.modeles;

import org.json.JSONException;
import org.json.JSONObject;

public class Seance {

    private String id;
    private String programId;
    private String title;
    private String description;
    private String dueDate;
    private String instructions;
    private String status;
    private Integer grade;
    private String comment;
    private int totalPoints;
    private String type;

    public Seance(JSONObject json) throws JSONException {
        this.id = json.optString("id");
        this.programId = json.optString("programId");
        this.title = json.optString("title");
        this.description = json.optString("description");
        this.dueDate = json.optString("dueDate");
        this.instructions = json.optString("instructions");
        this.status = json.optString("status");
        this.grade = json.isNull("grade") ? null : json.getInt("grade");
        this.comment = json.isNull("comment") ? null : json.getString("comment");
        this.totalPoints = json.optInt("totalPoints");
        this.type = json.optString("type");
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

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getGrade() {
        return grade;
    }

    public String getComment() {
        return comment;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public String getType() {
        return type;
    }
}
