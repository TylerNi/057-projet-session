package com.example.fitzone.modeles;

import org.json.JSONException;
import org.json.JSONObject;

public class Aliment {

    private final String id;
    private final String image;
    private final String nom;
    private final String description;
    private final int calories;
    private final String moment;

    public Aliment(JSONObject json) throws JSONException {
        id = json.optString("id");
        image = json.optString("image");
        nom = json.optString("nom");
        description = json.optString("description");
        calories = json.optInt("calories");
        moment = json.optString("moment");
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public String getMoment() {
        return moment;
    }
}
