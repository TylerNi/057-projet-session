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
import com.example.fitzone.adaptateurs.QuizAdapter;
import com.example.fitzone.dao.ResultatQuizDao;
import com.example.fitzone.modeles.Quiz;
import com.example.fitzone.modeles.QuizResult;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListeQuizActivity extends AppCompatActivity {

    public static final String EXTRA_PROGRAMME_ID = "programmeId";

    private String programmeId;
    private String userId;
    private RecyclerView listeQuiz;
    private TextView texteVide;
    private LinearProgressIndicator indicateurChargement;
    private ResultatQuizDao resultatQuizDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_quiz);

        programmeId = getIntent().getStringExtra(EXTRA_PROGRAMME_ID);
        userId = new SessionManager(this).obtenirUserId();
        if (programmeId == null || userId == null) {
            finish();
            return;
        }

        MaterialToolbar barreOutils = findViewById(R.id.barreOutils);
        barreOutils.setNavigationOnClickListener(v -> finish());

        listeQuiz = findViewById(R.id.listeQuiz);
        texteVide = findViewById(R.id.texteVide);
        indicateurChargement = findViewById(R.id.indicateurChargement);
        listeQuiz.setLayoutManager(new LinearLayoutManager(this));
        resultatQuizDao = new ResultatQuizDao(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerQuiz();
    }

    private void chargerQuiz() {
        indicateurChargement.setVisibility(View.VISIBLE);
        ApiClient.get("/quizzes", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray tableau = new JSONArray(body);
                    List<Quiz> quiz = new ArrayList<>();
                    for (int i = 0; i < tableau.length(); i++) {
                        Quiz item = new Quiz(tableau.getJSONObject(i));
                        if (programmeId.equals(item.getProgramId())) {
                            quiz.add(item);
                        }
                    }
                    afficherQuiz(quiz);
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

    private void afficherQuiz(List<Quiz> quiz) {
        indicateurChargement.setVisibility(View.GONE);
        texteVide.setVisibility(quiz.isEmpty() ? View.VISIBLE : View.GONE);

        Map<String, QuizResult> resultats = new HashMap<>();
        for (QuizResult resultat : resultatQuizDao.obtenirTous(userId)) {
            resultats.put(resultat.getQuizId(), resultat);
        }

        listeQuiz.setAdapter(new QuizAdapter(quiz, resultats, item -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, item.getId());
            startActivity(intent);
        }));
    }

    private void afficherErreur(String message) {
        indicateurChargement.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
