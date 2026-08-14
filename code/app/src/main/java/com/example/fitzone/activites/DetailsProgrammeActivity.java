package com.example.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.R;
import com.example.fitzone.modeles.Program;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

public class DetailsProgrammeActivity extends AppCompatActivity {

    public static final String EXTRA_PROGRAMME_ID = "programmeId";

    private String programmeId;
    private LinearProgressIndicator indicateurChargement;
    private View contenuProgramme;
    private TextView texteErreur;
    private TextView texteCode;
    private TextView texteTitre;
    private TextView texteCoach;
    private TextView texteSession;
    private Chip puceStatut;
    private TextView texteDescription;
    private LinearLayout conteneurAnnonces;
    private TextView texteResumeSeances;
    private TextView texteResumeQuiz;
    private MaterialButton boutonVoirSeances;
    private MaterialButton boutonVoirQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_programme);

        programmeId = getIntent().getStringExtra(EXTRA_PROGRAMME_ID);
        if (programmeId == null || programmeId.trim().isEmpty()) {
            Toast.makeText(this, R.string.details_programme_introuvable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar barreOutils = findViewById(R.id.barreOutils);
        barreOutils.setNavigationOnClickListener(v -> finish());

        indicateurChargement = findViewById(R.id.indicateurChargement);
        contenuProgramme = findViewById(R.id.contenuProgramme);
        texteErreur = findViewById(R.id.texteErreur);
        texteCode = findViewById(R.id.texteCodeProgramme);
        texteTitre = findViewById(R.id.texteTitreProgramme);
        texteCoach = findViewById(R.id.texteCoachProgramme);
        texteSession = findViewById(R.id.texteSessionProgramme);
        puceStatut = findViewById(R.id.puceStatutProgramme);
        texteDescription = findViewById(R.id.texteDescriptionProgramme);
        conteneurAnnonces = findViewById(R.id.conteneurAnnoncesProgramme);
        texteResumeSeances = findViewById(R.id.texteResumeSeances);
        texteResumeQuiz = findViewById(R.id.texteResumeQuiz);
        boutonVoirSeances = findViewById(R.id.boutonVoirSeances);
        boutonVoirQuiz = findViewById(R.id.boutonVoirQuiz);

        boutonVoirSeances.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeancesActivity.class);
            intent.putExtra(SeancesActivity.EXTRA_PROGRAMME_ID, programmeId);
            startActivity(intent);
        });
        boutonVoirQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListeQuizActivity.class);
            intent.putExtra(ListeQuizActivity.EXTRA_PROGRAMME_ID, programmeId);
            startActivity(intent);
        });

        chargerProgramme();
    }

    private void chargerProgramme() {
        ApiClient.get("/programs/" + programmeId, new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    Program programme = new Program(new JSONObject(body));
                    afficherProgramme(programme);
                    chargerResumeSeances();
                    chargerResumeQuiz();
                } catch (Exception e) {
                    afficherErreur(getString(R.string.details_programme_introuvable));
                }
            }

            @Override
            public void onError(String message) {
                afficherErreur(message);
            }
        });
    }

    private void afficherProgramme(Program programme) {
        indicateurChargement.setVisibility(View.GONE);
        contenuProgramme.setVisibility(View.VISIBLE);
        texteErreur.setVisibility(View.GONE);

        texteCode.setText(programme.getCode());
        texteTitre.setText(programme.getTitle());
        texteCoach.setText(getString(R.string.programme_coach, programme.getCoach()));
        texteSession.setText(programme.getSession());
        texteDescription.setText(programme.getDescription());

        String statut = programme.getStatut();
        if (statut == null || statut.isEmpty()) {
            puceStatut.setVisibility(View.GONE);
        } else {
            puceStatut.setText("termine".equals(statut)
                    ? R.string.programme_statut_termine : R.string.programme_statut_actif);
        }

        conteneurAnnonces.removeAllViews();
        if (programme.getAnnonces().isEmpty()) {
            ajouterAnnonce(getString(R.string.details_aucune_annonce));
        } else {
            for (String annonce : programme.getAnnonces()) {
                ajouterAnnonce("- " + annonce);
            }
        }
    }

    private void ajouterAnnonce(String annonce) {
        TextView vue = new TextView(this);
        vue.setText(annonce);
        vue.setTextSize(16);
        vue.setPadding(0, 6, 0, 6);
        conteneurAnnonces.addView(vue);
    }

    private void chargerResumeSeances() {
        ApiClient.get("/seances", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray tableau = new JSONArray(body);
                    int nombre = 0;
                    for (int i = 0; i < tableau.length(); i++) {
                        if (programmeId.equals(tableau.getJSONObject(i).optString("programId"))) {
                            nombre++;
                        }
                    }
                    texteResumeSeances.setText(getString(R.string.details_resume_seances, nombre));
                    boutonVoirSeances.setEnabled(nombre > 0);
                } catch (Exception e) {
                    texteResumeSeances.setText(getString(R.string.erreur_chargement));
                }
            }

            @Override
            public void onError(String message) {
                texteResumeSeances.setText(message);
            }
        });
    }

    private void chargerResumeQuiz() {
        ApiClient.get("/quizzes", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray tableau = new JSONArray(body);
                    int nombre = 0;
                    for (int i = 0; i < tableau.length(); i++) {
                        if (programmeId.equals(tableau.getJSONObject(i).optString("programId"))) {
                            nombre++;
                        }
                    }
                    texteResumeQuiz.setText(getString(R.string.details_resume_quiz, nombre));
                    boutonVoirQuiz.setEnabled(nombre > 0);
                } catch (Exception e) {
                    texteResumeQuiz.setText(getString(R.string.erreur_chargement));
                }
            }

            @Override
            public void onError(String message) {
                texteResumeQuiz.setText(message);
            }
        });
    }

    private void afficherErreur(String message) {
        indicateurChargement.setVisibility(View.GONE);
        contenuProgramme.setVisibility(View.GONE);
        texteErreur.setText(message);
        texteErreur.setVisibility(View.VISIBLE);
    }
}
