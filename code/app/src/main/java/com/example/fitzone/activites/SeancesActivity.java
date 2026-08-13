package com.example.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.adaptateurs.SeanceAdapter;
import com.example.fitzone.dao.EtatSeanceDao;
import com.example.fitzone.modeles.Seance;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.SessionManager;
import com.example.fitzone.utils.StatutSeance;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeancesActivity extends AppCompatActivity {

    public static final String EXTRA_PROGRAMME_ID = "programmeId";

    private String programmeId;
    private RecyclerView listeSeances;
    private TextView texteVide;
    private LinearProgressIndicator indicateurChargement;
    private EtatSeanceDao etatSeanceDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seances);

        programmeId = getIntent().getStringExtra(EXTRA_PROGRAMME_ID);
        if (programmeId == null || programmeId.trim().isEmpty()) {
            finish();
            return;
        }

        MaterialToolbar barreOutils = findViewById(R.id.barreOutils);
        barreOutils.setNavigationOnClickListener(v -> finish());

        listeSeances = findViewById(R.id.listeSeances);
        texteVide = findViewById(R.id.texteVide);
        indicateurChargement = findViewById(R.id.indicateurChargement);
        listeSeances.setLayoutManager(new LinearLayoutManager(this));

        etatSeanceDao = new EtatSeanceDao(this);
        sessionManager = new SessionManager(this);
        if (sessionManager.obtenirUserId() == null) {
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerSeances();
    }

    private void chargerSeances() {
        indicateurChargement.setVisibility(View.VISIBLE);
        ApiClient.get("/seances", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray tableau = new JSONArray(body);
                    List<Seance> seances = new ArrayList<>();
                    for (int i = 0; i < tableau.length(); i++) {
                        Seance seance = new Seance(tableau.getJSONObject(i));
                        if (programmeId.equals(seance.getProgramId())) {
                            seances.add(seance);
                        }
                    }
                    afficherSeances(seances);
                } catch (Exception e) {
                    afficherErreur(getString(R.string.erreur_chargement));
                }
            }

            @Override
            public void onError(String message) {
                afficherErreur(message);
            }
        });
    }

    private void afficherSeances(List<Seance> seances) {
        indicateurChargement.setVisibility(View.GONE);
        texteVide.setVisibility(seances.isEmpty() ? View.VISIBLE : View.GONE);

        String userId = sessionManager.obtenirUserId();
        Map<String, String> statutsLocaux = etatSeanceDao.obtenirStatuts(userId);
        Map<String, String> statutsAffiches = new HashMap<>();
        for (Seance seance : seances) {
            statutsAffiches.put(seance.getId(),
                    StatutSeance.calculer(seance, statutsLocaux.get(seance.getId())));
        }

        listeSeances.setAdapter(new SeanceAdapter(seances, statutsAffiches, seance -> {
            Intent intent = new Intent(this, DetailSeanceActivity.class);
            intent.putExtra(DetailSeanceActivity.EXTRA_SEANCE_ID, seance.getId());
            startActivity(intent);
        }));
    }

    private void afficherErreur(String message) {
        indicateurChargement.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
