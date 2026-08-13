package com.example.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.adaptateurs.ProgrammeAdapter;
import com.example.fitzone.modeles.Program;
import com.example.fitzone.modeles.User;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.SessionManager;
import com.example.fitzone.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListeProgrammesActivity extends AppCompatActivity {

    private EditText champRecherche;
    private ChipGroup groupeFiltres;
    private TextView texteVide;
    private RecyclerView listeProgrammes;

    private ProgrammeAdapter adapter;
    private List<Program> tousLesProgrammes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_programmes);
        setTitle(R.string.programmes_titre);

        champRecherche = findViewById(R.id.champRecherche);
        groupeFiltres = findViewById(R.id.groupeFiltres);
        texteVide = findViewById(R.id.texteVide);
        listeProgrammes = findViewById(R.id.listeProgrammes);

        adapter = new ProgrammeAdapter(new ArrayList<>(), programme -> {
            Intent intent = new Intent(this, DetailsProgrammeActivity.class);
            intent.putExtra(DetailsProgrammeActivity.EXTRA_PROGRAMME_ID, programme.getId());
            startActivity(intent);
        });
        listeProgrammes.setLayoutManager(new LinearLayoutManager(this));
        listeProgrammes.setAdapter(adapter);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        NavigationHelper.configurer(this, navigation, R.id.navProgrammes);

        champRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                appliquerFiltres();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        groupeFiltres.setOnCheckedStateChangeListener((group, checkedIds) -> appliquerFiltres());

        charger();
    }

    private void charger() {
        SessionManager session = new SessionManager(this);
        String userId = session.obtenirUserId();

        ApiClient.get("/users/" + userId, new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    User user = new User(new JSONObject(body));
                    chargerProgrammes(user.getEnrolledProgramIds());
                } catch (Exception e) {
                    afficherMessage(getString(R.string.erreur_chargement));
                }
            }

            @Override
            public void onError(String message) {
                afficherMessage(message);
            }
        });
    }

    private void chargerProgrammes(List<String> idsInscrits) {
        ApiClient.get("/programs", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray tableau = new JSONArray(body);
                    tousLesProgrammes.clear();
                    for (int i = 0; i < tableau.length(); i++) {
                        Program programme = new Program(tableau.getJSONObject(i));
                        if (idsInscrits.contains(programme.getId())) {
                            tousLesProgrammes.add(programme);
                        }
                    }
                    appliquerFiltres();
                } catch (Exception e) {
                    afficherMessage(getString(R.string.erreur_chargement));
                }
            }

            @Override
            public void onError(String message) {
                afficherMessage(message);
            }
        });
    }

    private void appliquerFiltres() {
        String recherche = champRecherche.getText().toString().trim().toLowerCase(Locale.getDefault());
        int filtreCoche = groupeFiltres.getCheckedChipId();

        List<Program> filtres = new ArrayList<>();
        for (Program programme : tousLesProgrammes) {
            boolean correspondRecherche = recherche.isEmpty()
                    || programme.getTitle().toLowerCase(Locale.getDefault()).contains(recherche)
                    || programme.getCode().toLowerCase(Locale.getDefault()).contains(recherche);

            boolean correspondFiltre = filtreCoche == R.id.filtreTous
                    || (filtreCoche == R.id.filtreActifs && "actif".equals(programme.getStatut()))
                    || (filtreCoche == R.id.filtreTermines && "termine".equals(programme.getStatut()));

            if (correspondRecherche && correspondFiltre) {
                filtres.add(programme);
            }
        }

        adapter.mettreAJour(filtres);
        texteVide.setVisibility(filtres.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void afficherMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
