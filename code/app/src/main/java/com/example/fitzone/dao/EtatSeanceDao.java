package com.example.fitzone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class EtatSeanceDao {

    private BaseSQLite base;

    public EtatSeanceDao(Context context) {
        this.base = new BaseSQLite(context);
    }

    public void enregistrer(String userId, String seanceId, String statut, String dateSoumission, String contenu) {
        SQLiteDatabase db = base.getWritableDatabase();
        ContentValues valeurs = new ContentValues();
        valeurs.put("userId", userId);
        valeurs.put("seanceId", seanceId);
        valeurs.put("statut", statut);
        valeurs.put("dateSoumission", dateSoumission);
        valeurs.put("contenu", contenu);
        db.replace(BaseSQLite.TABLE_ETAT_SEANCE, null, valeurs);
        db.close();
    }

    public String obtenirStatut(String userId, String seanceId) {
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor curseur = db.query(BaseSQLite.TABLE_ETAT_SEANCE, new String[]{"statut"},
                "userId = ? AND seanceId = ?", new String[]{userId, seanceId}, null, null, null);

        String statut = null;
        if (curseur.moveToFirst()) {
            statut = curseur.getString(0);
        }
        curseur.close();
        db.close();
        return statut;
    }

    public String obtenirDateSoumission(String userId, String seanceId) {
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor curseur = db.query(BaseSQLite.TABLE_ETAT_SEANCE, new String[]{"dateSoumission"},
                "userId = ? AND seanceId = ?", new String[]{userId, seanceId}, null, null, null);

        String date = null;
        if (curseur.moveToFirst()) {
            date = curseur.getString(0);
        }
        curseur.close();
        db.close();
        return date;
    }

    public String obtenirContenu(String userId, String seanceId) {
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor curseur = db.query(BaseSQLite.TABLE_ETAT_SEANCE, new String[]{"contenu"},
                "userId = ? AND seanceId = ?", new String[]{userId, seanceId}, null, null, null);

        String contenu = null;
        if (curseur.moveToFirst()) {
            contenu = curseur.getString(0);
        }
        curseur.close();
        db.close();
        return contenu;
    }

    public Map<String, String> obtenirStatuts(String userId) {
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor curseur = db.query(BaseSQLite.TABLE_ETAT_SEANCE, new String[]{"seanceId", "statut"},
                "userId = ?", new String[]{userId}, null, null, null);

        Map<String, String> statuts = new HashMap<>();
        while (curseur.moveToNext()) {
            statuts.put(curseur.getString(0), curseur.getString(1));
        }
        curseur.close();
        db.close();
        return statuts;
    }
}
