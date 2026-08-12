package com.example.fitzone.modeles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Program {

    private String id;
    private String code;
    private String title;
    private String description;
    private String coach;
    private String session;
    private String imageUrl;
    private String statut;
    private List<String> annonces;

    public Program(JSONObject json) throws JSONException {
        this.id = json.optString("id");
        this.code = json.optString("code");
        this.title = json.optString("title");
        this.description = json.optString("description");
        this.coach = json.optString("coach");
        this.session = json.optString("session");
        this.imageUrl = json.optString("imageUrl");
        this.statut = json.optString("statut");
        this.annonces = new ArrayList<>();

        JSONArray annoncesJson = json.optJSONArray("annonces");
        if (annoncesJson != null) {
            for (int i = 0; i < annoncesJson.length(); i++) {
                this.annonces.add(annoncesJson.getString(i));
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCoach() {
        return coach;
    }

    public String getSession() {
        return session;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatut() {
        return statut;
    }

    public List<String> getAnnonces() {
        return annonces;
    }
}
