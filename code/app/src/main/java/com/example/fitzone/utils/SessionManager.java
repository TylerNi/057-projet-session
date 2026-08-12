package com.example.fitzone.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String FICHIER = "fitzone_session";
    private static final String CLE_USER_ID = "userId";

    private SharedPreferences preferences;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(FICHIER, Context.MODE_PRIVATE);
    }

    public void ouvrirSession(String userId) {
        preferences.edit().putString(CLE_USER_ID, userId).apply();
    }

    public String obtenirUserId() {
        return preferences.getString(CLE_USER_ID, null);
    }

    public boolean estConnecte() {
        return obtenirUserId() != null;
    }

    public void fermerSession() {
        preferences.edit().remove(CLE_USER_ID).apply();
    }
}
