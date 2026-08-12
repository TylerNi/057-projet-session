package com.example.fitzone.modeles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String username;
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private String telephone;
    private String photoUrl;
    private List<String> enrolledProgramIds;
    private List<QuizResult> quizResults;
    private List<String> completedSeanceIds;

    public User() {
        this.enrolledProgramIds = new ArrayList<>();
        this.quizResults = new ArrayList<>();
        this.completedSeanceIds = new ArrayList<>();
    }

    public User(JSONObject json) throws JSONException {
        this();
        this.id = json.optString("id");
        this.username = json.optString("username");
        this.email = json.optString("email");
        this.password = json.optString("password");
        this.nom = json.optString("nom");
        this.prenom = json.optString("prenom");
        this.telephone = json.optString("telephone");
        this.photoUrl = json.optString("photoUrl");

        JSONArray programs = json.optJSONArray("enrolledProgramIds");
        if (programs != null) {
            for (int i = 0; i < programs.length(); i++) {
                this.enrolledProgramIds.add(programs.getString(i));
            }
        }

        JSONArray results = json.optJSONArray("quizResults");
        if (results != null) {
            for (int i = 0; i < results.length(); i++) {
                this.quizResults.add(new QuizResult(results.getJSONObject(i)));
            }
        }

        JSONArray seances = json.optJSONArray("completedSeanceIds");
        if (seances != null) {
            for (int i = 0; i < seances.length(); i++) {
                this.completedSeanceIds.add(seances.getString(i));
            }
        }
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        if (id != null) {
            json.put("id", id);
        }
        json.put("username", username);
        json.put("email", email);
        json.put("password", password);
        json.put("nom", nom);
        json.put("prenom", prenom);
        json.put("telephone", telephone);
        json.put("photoUrl", photoUrl);
        json.put("enrolledProgramIds", new JSONArray(enrolledProgramIds));

        JSONArray results = new JSONArray();
        for (QuizResult result : quizResults) {
            results.put(result.toJson());
        }
        json.put("quizResults", results);
        json.put("completedSeanceIds", new JSONArray(completedSeanceIds));
        return json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getEnrolledProgramIds() {
        return enrolledProgramIds;
    }

    public List<QuizResult> getQuizResults() {
        return quizResults;
    }

    public List<String> getCompletedSeanceIds() {
        return completedSeanceIds;
    }
}
