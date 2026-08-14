package com.example.fitzone.utils;

import com.example.fitzone.R;
import com.example.fitzone.modeles.Seance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatutSeance {

    public static final String A_FAIRE = "À faire";
    public static final String A_VENIR = "À venir";
    public static final String SOUMISE = "Soumise";
    public static final String EN_RETARD = "En retard";
    public static final String VALIDEE = "Validée";

    public static String calculer(Seance seance, String statutLocal) {
        if (seance.getGrade() != null) {
            return VALIDEE;
        }
        if (!estDisponible(seance)) {
            return A_VENIR;
        }
        if (SOUMISE.equals(statutLocal)) {
            return SOUMISE;
        }
        if (seance.getDueDate() != null && seance.getDueDate().compareTo(aujourdhui()) < 0) {
            return EN_RETARD;
        }
        return A_FAIRE;
    }

    public static boolean estDisponible(Seance seance) {
        String dateDisponibilite = seance.getAvailableDate();
        return dateDisponibilite == null || dateDisponibilite.isEmpty()
                || dateDisponibilite.compareTo(aujourdhui()) <= 0;
    }

    public static int couleur(String statut) {
        if (VALIDEE.equals(statut)) {
            return R.color.statut_validee;
        }
        if (SOUMISE.equals(statut)) {
            return R.color.statut_soumise;
        }
        if (EN_RETARD.equals(statut)) {
            return R.color.statut_en_retard;
        }
        if (A_VENIR.equals(statut)) {
            return R.color.fitzone_text_secondary;
        }
        return R.color.fitzone_primary;
    }

    public static String aujourdhui() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA).format(new Date());
    }
}
