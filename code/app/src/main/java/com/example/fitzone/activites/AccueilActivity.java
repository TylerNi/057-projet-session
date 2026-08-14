package com.example.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.adaptateurs.ProgrammeAdapter;
import com.example.fitzone.adaptateurs.SeanceApercuAdapter;
import com.example.fitzone.dao.EtatSeanceDao;
import com.example.fitzone.dao.ResultatQuizDao;
import com.example.fitzone.dao.UtilisateurDao;
import com.example.fitzone.modeles.Program;
import com.example.fitzone.modeles.Quiz;
import com.example.fitzone.modeles.QuizResult;
import com.example.fitzone.modeles.Seance;
import com.example.fitzone.modeles.User;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.SessionManager;
import com.example.fitzone.utils.StatutSeance;
import com.example.fitzone.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccueilActivity extends AppCompatActivity {

    private static final int MAX_APERCU = 3;

    private TextView texteBienvenue;
    private TextView texteResume;
    private TextView texteStatuts;
    private RecyclerView listeProgrammes;
    private RecyclerView listeSeances;
    private LinearLayout conteneurAnnonces;
    private Button boutonTousLesProgrammes;

    private SessionManager session;
    private EtatSeanceDao etatSeanceDao;
    private ResultatQuizDao resultatQuizDao;

    private List<String> idsInscrits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        setTitle(R.string.accueil_titre);

        texteBienvenue = findViewById(R.id.texteBienvenue);
        texteResume = findViewById(R.id.texteResume);
        texteStatuts = findViewById(R.id.texteStatuts);
        listeProgrammes = findViewById(R.id.listeProgrammes);
        listeSeances = findViewById(R.id.listeSeances);
        conteneurAnnonces = findViewById(R.id.conteneurAnnonces);
        boutonTousLesProgrammes = findViewById(R.id.boutonTousLesProgrammes);

        session = new SessionManager(this);
        etatSeanceDao = new EtatSeanceDao(this);
        resultatQuizDao = new ResultatQuizDao(this);

        listeProgrammes.setLayoutManager(new LinearLayoutManager(this));
        listeProgrammes.setNestedScrollingEnabled(false);
        listeSeances.setLayoutManager(new LinearLayoutManager(this));
        listeSeances.setNestedScrollingEnabled(false);

        boutonTousLesProgrammes.setOnClickListener(v ->
                startActivity(new Intent(this, ListeProgrammesActivity.class)));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        NavigationHelper.configurer(this, navigation, R.id.navAccueil);

        UtilisateurDao utilisateurDao = new UtilisateurDao(this);
        User cache = utilisateurDao.obtenir(session.obtenirUserId());
        if (cache != null) {
            texteBienvenue.setText(getString(R.string.accueil_bienvenue, cache.getPrenom()));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerUtilisateur();
    }

    private void chargerUtilisateur() {
        ApiClient.get("/users/" + session.obtenirUserId(), new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    User user = new User(new JSONObject(body));
                    idsInscrits = user.getEnrolledProgramIds();
                    texteBienvenue.setText(getString(R.string.accueil_bienvenue, user.getPrenom()));
                    chargerProgrammes();
                } catch (Exception e) {
                    Toast.makeText(AccueilActivity.this, R.string.erreur_chargement,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AccueilActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chargerProgrammes() {
        ApiClient.get("/programs", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    List<Program> programmes = new ArrayList<>();
                    JSONArray tableau = new JSONArray(body);
                    for (int i = 0; i < tableau.length(); i++) {
                        Program programme = new Program(tableau.getJSONObject(i));
                        if (idsInscrits.contains(programme.getId())) {
                            programmes.add(programme);
                        }
                    }
                    afficherProgrammes(programmes);
                    afficherAnnonces(programmes);
                    chargerSeances(programmes.size());
                } catch (Exception e) {
                    Toast.makeText(AccueilActivity.this, R.string.erreur_chargement,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AccueilActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void afficherProgrammes(List<Program> programmes) {
        List<Program> apercu = programmes.subList(0, Math.min(MAX_APERCU, programmes.size()));
        listeProgrammes.setAdapter(new ProgrammeAdapter(new ArrayList<>(apercu), programme -> {
            Intent intent = new Intent(this, DetailsProgrammeActivity.class);
            intent.putExtra(DetailsProgrammeActivity.EXTRA_PROGRAMME_ID, programme.getId());
            startActivity(intent);
        }));
    }

    private void afficherAnnonces(List<Program> programmes) {
        conteneurAnnonces.removeAllViews();
        int nombreAffiche = 0;
        for (Program programme : programmes) {
            for (String annonce : programme.getAnnonces()) {
                if (nombreAffiche >= MAX_APERCU) {
                    break;
                }

                View carte = LayoutInflater.from(this)
                        .inflate(R.layout.item_annonce, conteneurAnnonces, false);
                TextView texteProgramme = carte.findViewById(R.id.texteProgrammeAnnonce);
                TextView texteAnnonce = carte.findViewById(R.id.texteAnnonce);
                texteProgramme.setText(getString(R.string.accueil_annonce_programme,
                        programme.getCode(), programme.getTitle()));
                texteAnnonce.setText(annonce);
                carte.setOnClickListener(v -> {
                    Intent intent = new Intent(this, DetailsProgrammeActivity.class);
                    intent.putExtra(DetailsProgrammeActivity.EXTRA_PROGRAMME_ID, programme.getId());
                    startActivity(intent);
                });
                conteneurAnnonces.addView(carte);
                nombreAffiche++;
            }
            if (nombreAffiche >= MAX_APERCU) {
                break;
            }
        }

        if (conteneurAnnonces.getChildCount() == 0) {
            TextView vue = new TextView(this);
            vue.setText(R.string.accueil_aucune_annonce);
            vue.setGravity(Gravity.CENTER);
            vue.setTextColor(getColor(R.color.fitzone_text_secondary));
            vue.setPadding(16, 24, 16, 24);
            conteneurAnnonces.addView(vue);
        }
    }

    private void chargerSeances(int nombreProgrammes) {
        ApiClient.get("/seances", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    List<Seance> seances = new ArrayList<>();
                    JSONArray tableau = new JSONArray(body);
                    for (int i = 0; i < tableau.length(); i++) {
                        Seance seance = new Seance(tableau.getJSONObject(i));
                        if (idsInscrits.contains(seance.getProgramId())) {
                            seances.add(seance);
                        }
                    }
                    afficherSeances(seances, nombreProgrammes);
                } catch (Exception e) {
                    Toast.makeText(AccueilActivity.this, R.string.erreur_chargement,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AccueilActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void afficherSeances(List<Seance> seances, int nombreProgrammes) {
        Map<String, String> statutsLocaux = etatSeanceDao.obtenirStatuts(session.obtenirUserId());
        Map<String, String> statuts = new HashMap<>();
        int aFaire = 0;
        int enRetard = 0;
        int soumises = 0;
        int validees = 0;

        for (Seance seance : seances) {
            String statut = StatutSeance.calculer(seance, statutsLocaux.get(seance.getId()));
            statuts.put(seance.getId(), statut);

            if (StatutSeance.VALIDEE.equals(statut)) {
                validees++;
            } else if (StatutSeance.SOUMISE.equals(statut)) {
                soumises++;
            } else if (StatutSeance.EN_RETARD.equals(statut)) {
                enRetard++;
            } else {
                aFaire++;
            }
        }

        texteStatuts.setText(getString(R.string.accueil_statuts, soumises, validees, enRetard, aFaire));

        List<Seance> aVenir = new ArrayList<>();
        for (Seance seance : seances) {
            String statut = statuts.get(seance.getId());
            if (StatutSeance.A_FAIRE.equals(statut)
                    || StatutSeance.A_VENIR.equals(statut)
                    || StatutSeance.EN_RETARD.equals(statut)) {
                aVenir.add(seance);
            }
        }
        Collections.sort(aVenir, (a, b) -> a.getDueDate().compareTo(b.getDueDate()));
        List<Seance> apercu = new ArrayList<>(aVenir.subList(0, Math.min(MAX_APERCU, aVenir.size())));

        listeSeances.setAdapter(new SeanceApercuAdapter(apercu, statuts));

        chargerQuiz(nombreProgrammes);
    }

    private void chargerQuiz(int nombreProgrammes) {
        ApiClient.get("/quizzes", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    List<QuizResult> resultats = resultatQuizDao.obtenirTous(session.obtenirUserId());
                    List<String> quizFaits = new ArrayList<>();
                    for (QuizResult resultat : resultats) {
                        quizFaits.add(resultat.getQuizId());
                    }

                    int disponibles = 0;
                    JSONArray tableau = new JSONArray(body);
                    for (int i = 0; i < tableau.length(); i++) {
                        Quiz quiz = new Quiz(tableau.getJSONObject(i));
                        if (idsInscrits.contains(quiz.getProgramId()) && !quizFaits.contains(quiz.getId())) {
                            disponibles++;
                        }
                    }

                    texteResume.setText(getString(R.string.accueil_resume, nombreProgrammes, disponibles));
                } catch (Exception e) {
                    Toast.makeText(AccueilActivity.this, R.string.erreur_chargement,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AccueilActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
