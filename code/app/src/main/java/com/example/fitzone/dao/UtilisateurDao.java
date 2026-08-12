package com.example.fitzone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fitzone.modeles.User;

public class UtilisateurDao {

    private BaseSQLite base;

    public UtilisateurDao(Context context) {
        this.base = new BaseSQLite(context);
    }

    public void sauvegarder(User user) {
        SQLiteDatabase db = base.getWritableDatabase();
        ContentValues valeurs = new ContentValues();
        valeurs.put("id", user.getId());
        valeurs.put("username", user.getUsername());
        valeurs.put("email", user.getEmail());
        valeurs.put("nom", user.getNom());
        valeurs.put("prenom", user.getPrenom());
        valeurs.put("telephone", user.getTelephone());
        valeurs.put("photoUrl", user.getPhotoUrl());
        db.replace(BaseSQLite.TABLE_UTILISATEUR, null, valeurs);
        db.close();
    }

    public User obtenir(String userId) {
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor curseur = db.query(BaseSQLite.TABLE_UTILISATEUR, null, "id = ?",
                new String[]{userId}, null, null, null);

        User user = null;
        if (curseur.moveToFirst()) {
            user = new User();
            user.setId(curseur.getString(curseur.getColumnIndexOrThrow("id")));
            user.setUsername(curseur.getString(curseur.getColumnIndexOrThrow("username")));
            user.setEmail(curseur.getString(curseur.getColumnIndexOrThrow("email")));
            user.setNom(curseur.getString(curseur.getColumnIndexOrThrow("nom")));
            user.setPrenom(curseur.getString(curseur.getColumnIndexOrThrow("prenom")));
            user.setTelephone(curseur.getString(curseur.getColumnIndexOrThrow("telephone")));
            user.setPhotoUrl(curseur.getString(curseur.getColumnIndexOrThrow("photoUrl")));
        }
        curseur.close();
        db.close();
        return user;
    }

    public void supprimer(String userId) {
        SQLiteDatabase db = base.getWritableDatabase();
        db.delete(BaseSQLite.TABLE_UTILISATEUR, "id = ?", new String[]{userId});
        db.close();
    }
}
