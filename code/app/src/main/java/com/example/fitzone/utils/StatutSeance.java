package com.example.fitzone.utils;

import com.example.fitzone.modeles.Seance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatutSeance {

    public static final String A_FAIRE = "À faire";
    public static final String SOUMISE = "Soumise";
    public static final String EN_RETARD = "En retard";
    public static final String VALIDEE = "Validée";

    public static String calculer(Seance seance, String statutLocal) {
        if (seance.getGrade() != null) {
            return VALIDEE;
        }
        if (SOUMISE.equals(statutLocal)) {
            return SOUMISE;
        }
        if (seance.getDueDate() != null && seance.getDueDate().compareTo(aujourdhui()) < 0) {
            return EN_RETARD;
        }
        return A_FAIRE;
    }

    public static String aujourdhui() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA).format(new Date());
    }
}
