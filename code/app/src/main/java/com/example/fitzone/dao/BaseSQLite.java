package com.example.fitzone.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseSQLite extends SQLiteOpenHelper {

    public static final String NOM_BASE = "fitzone.db";
    public static final int VERSION = 1;

    public static final String TABLE_UTILISATEUR = "utilisateur";
    public static final String TABLE_RESULTAT_QUIZ = "resultat_quiz";
    public static final String TABLE_ETAT_SEANCE = "etat_seance";

    public BaseSQLite(Context context) {
        super(context, NOM_BASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_UTILISATEUR + " ("
                + "id TEXT PRIMARY KEY, "
                + "username TEXT, "
                + "email TEXT, "
                + "nom TEXT, "
                + "prenom TEXT, "
                + "telephone TEXT, "
                + "photoUrl TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RESULTAT_QUIZ + " ("
                + "userId TEXT, "
                + "quizId TEXT, "
                + "score INTEGER, "
                + "total INTEGER, "
                + "PRIMARY KEY (userId, quizId))");

        db.execSQL("CREATE TABLE " + TABLE_ETAT_SEANCE + " ("
                + "userId TEXT, "
                + "seanceId TEXT, "
                + "statut TEXT, "
                + "dateSoumission TEXT, "
                + "contenu TEXT, "
                + "PRIMARY KEY (userId, seanceId))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ancienneVersion, int nouvelleVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTILISATEUR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTAT_QUIZ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ETAT_SEANCE);
        onCreate(db);
    }
}
