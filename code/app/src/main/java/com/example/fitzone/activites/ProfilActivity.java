package com.example.fitzone.activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.R;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        setTitle(R.string.nav_profil);
    }
}
