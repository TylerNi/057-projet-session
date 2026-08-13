package com.example.fitzone.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activite.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0);
            activite.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0);
        }

        navigation.getMenu().findItem(destinationActuelle).setChecked(true);
        navigation.setItemHorizontalTranslationEnabled(false);
        navigation.setItemActiveIndicatorEnabled(false);
        navigation.setOnItemReselectedListener(item -> {
        });
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
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activite.startActivity(intent,
                        ActivityOptions.makeCustomAnimation(activite, 0, 0).toBundle());
                return true;
            }
            return false;
        });

        activite.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                navigation.getMenu().findItem(destinationActuelle).setChecked(true);
            }
        });

    }
}
