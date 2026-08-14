package com.example.fitzone.utils;

import android.app.ActivityOptions;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.R;
import com.example.fitzone.activites.AccueilActivity;
import com.example.fitzone.activites.ListeProgrammesActivity;
import com.example.fitzone.activites.NutritionActivity;
import com.example.fitzone.activites.ProfilActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class NavigationHelper {

    private NavigationHelper() {
    }

    public static void configurer(AppCompatActivity activite,
                                  BottomNavigationView navigation,
                                  int destinationActuelle) {
        navigation.getMenu().findItem(destinationActuelle).setChecked(true);
        navigation.setItemHorizontalTranslationEnabled(false);
        navigation.setItemActiveIndicatorEnabled(false);
        navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == destinationActuelle) {
                return true;
            }

            Class<?> destination = null;
            if (id == R.id.navAccueil) {
                destination = AccueilActivity.class;
            } else if (id == R.id.navProgrammes) {
                destination = ListeProgrammesActivity.class;
            } else if (id == R.id.navNutrition) {
                destination = NutritionActivity.class;
            } else if (id == R.id.navProfil) {
                destination = ProfilActivity.class;
            }

            if (destination != null) {
                Intent intent = new Intent(activite, destination);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activite.startActivity(intent,
                        ActivityOptions.makeCustomAnimation(activite, 0, 0).toBundle());
                navigation.post(() -> navigation.getMenu()
                        .findItem(destinationActuelle).setChecked(true));
                return true;
            }
            return false;
        });
    }
}
