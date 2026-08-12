package com.example.fitzone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fitzone.modeles.QuizResult;

import java.util.ArrayList;
import java.util.List;

public class ResultatQuizDao {

    private BaseSQLite base;

    public ResultatQuizDao(Context context) {
        this.base = new BaseSQLite(context);
    }

    public void enregistrer(String userId, QuizResult resultat) {
        SQLiteDatabase db = base.getWritableDatabase();
        ContentValues valeurs = new ContentValues();
        valeurs.put("userId", userId);
        valeurs.put("quizId", resultat.getQuizId());
        valeurs.put("score", resultat.getScore());
        valeurs.put("total", resultat.getTotal());
        db.replace(BaseSQLite.TABLE_RESULTAT_QUIZ, null, valeurs);
        db.close();
    }

    public QuizResult obtenir(String userId, String quizId) {
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor curseur = db.query(BaseSQLite.TABLE_RESULTAT_QUIZ, null, "userId = ? AND quizId = ?",
                new String[]{userId, quizId}, null, null, null);

        QuizResult resultat = null;
        if (curseur.moveToFirst()) {
            resultat = construire(curseur);
        }
        curseur.close();
        db.close();
        return resultat;
    }

    public List<QuizResult> obtenirTous(String userId) {
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor curseur = db.query(BaseSQLite.TABLE_RESULTAT_QUIZ, null, "userId = ?",
                new String[]{userId}, null, null, null);

        List<QuizResult> resultats = new ArrayList<>();
        while (curseur.moveToNext()) {
            resultats.add(construire(curseur));
        }
        curseur.close();
        db.close();
        return resultats;
    }

    private QuizResult construire(Cursor curseur) {
        return new QuizResult(
                curseur.getString(curseur.getColumnIndexOrThrow("quizId")),
                curseur.getInt(curseur.getColumnIndexOrThrow("score")),
                curseur.getInt(curseur.getColumnIndexOrThrow("total")));
    }
}
