package com.example.fitzone.activites;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.R;

public class DetailsProgrammeActivity extends AppCompatActivity {

    public static final String EXTRA_PROGRAMME_ID = "programmeId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_programme);

        String programmeId = getIntent().getStringExtra(EXTRA_PROGRAMME_ID);

        TextView texteId = findViewById(R.id.texteProgrammeId);
        texteId.setText(programmeId);
    }
}
