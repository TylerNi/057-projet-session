package com.example.fitzone.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.fitzone.R;
import com.example.fitzone.dao.ResultatQuizDao;
import com.example.fitzone.modeles.Question;
import com.example.fitzone.modeles.Quiz;
import com.example.fitzone.modeles.QuizResult;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_QUIZ_ID = "quizId";

    private String quizId;
    private String userId;
    private Quiz quiz;
    private ResultatQuizDao resultatQuizDao;
    private final List<RadioGroup> groupesReponses = new ArrayList<>();

    private LinearProgressIndicator indicateurChargement;
    private View contenuQuiz;
    private TextView texteTitre;
    private LinearLayout conteneurQuestions;
    private TextView texteResultat;
    private MaterialButton boutonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizId = getIntent().getStringExtra(EXTRA_QUIZ_ID);
        userId = new SessionManager(this).obtenirUserId();
        if (quizId == null || userId == null) {
            finish();
            return;
        }

        MaterialToolbar barreOutils = findViewById(R.id.barreOutils);
        barreOutils.setNavigationOnClickListener(v -> finish());

        indicateurChargement = findViewById(R.id.indicateurChargement);
        contenuQuiz = findViewById(R.id.contenuQuiz);
        texteTitre = findViewById(R.id.texteTitreQuiz);
        conteneurQuestions = findViewById(R.id.conteneurQuestions);
        texteResultat = findViewById(R.id.texteResultatQuiz);
        boutonValider = findViewById(R.id.boutonValiderQuiz);
        resultatQuizDao = new ResultatQuizDao(this);

        boutonValider.setOnClickListener(v -> validerReponses());
        chargerQuiz();
    }

    private void chargerQuiz() {
        ApiClient.get("/quizzes/" + quizId, new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    quiz = new Quiz(new JSONObject(body));
                    afficherQuiz();
                } catch (Exception e) {
                    afficherErreur(getString(R.string.quiz_introuvable));
                }
            }

            @Override
            public void onError(String message) {
                afficherErreur(message);
            }
        });
    }

    private void afficherQuiz() {
        indicateurChargement.setVisibility(View.GONE);
        contenuQuiz.setVisibility(View.VISIBLE);
        texteTitre.setText(quiz.getTitle());
        conteneurQuestions.removeAllViews();
        groupesReponses.clear();

        int numero = 1;
        for (Question question : quiz.getQuestions()) {
            MaterialCardView carte = new MaterialCardView(this);
            LinearLayout.LayoutParams paramsCarte = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsCarte.setMargins(0, dp(8), 0, dp(8));
            carte.setLayoutParams(paramsCarte);
            carte.setCardElevation(0);
            carte.setRadius(dp(12));
            carte.setStrokeWidth(dp(1));
            carte.setStrokeColor(ContextCompat.getColor(this, R.color.fitzone_border));
            carte.setCardBackgroundColor(ContextCompat.getColor(this, R.color.fitzone_surface));

            LinearLayout contenuCarte = new LinearLayout(this);
            contenuCarte.setOrientation(LinearLayout.VERTICAL);
            contenuCarte.setPadding(dp(16), dp(16), dp(16), dp(16));

            TextView enonce = new TextView(this);
            enonce.setText(getString(R.string.quiz_numero_question, numero, question.getQuestion()));
            enonce.setTextSize(17);
            enonce.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium);
            contenuCarte.addView(enonce);

            RadioGroup groupe = new RadioGroup(this);
            groupe.setOrientation(RadioGroup.VERTICAL);
            for (int i = 0; i < question.getOptions().size(); i++) {
                RadioButton choix = new RadioButton(this);
                choix.setId(View.generateViewId());
                choix.setTag(i);
                choix.setText(question.getOptions().get(i));
                choix.setPadding(0, dp(4), 0, dp(4));
                groupe.addView(choix);
            }
            contenuCarte.addView(groupe);
            carte.addView(contenuCarte);
            conteneurQuestions.addView(carte);
            groupesReponses.add(groupe);
            numero++;
        }

        QuizResult precedent = resultatQuizDao.obtenir(userId, quizId);
        if (precedent != null) {
            afficherResultat(precedent.getScore(), precedent.getTotal());
        }
    }

    private void validerReponses() {
        int score = 0;
        for (int i = 0; i < groupesReponses.size(); i++) {
            RadioGroup groupe = groupesReponses.get(i);
            int idCoche = groupe.getCheckedRadioButtonId();
            if (idCoche == -1) {
                Toast.makeText(this, R.string.quiz_toutes_reponses, Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton choix = groupe.findViewById(idCoche);
            int indexChoisi = (int) choix.getTag();
            if (indexChoisi == quiz.getQuestions().get(i).getCorrectOption()) {
                score++;
            }
        }

        QuizResult resultat = new QuizResult(quizId, score, quiz.getQuestions().size());
        resultatQuizDao.enregistrer(userId, resultat);
        afficherResultat(score, quiz.getQuestions().size());
        Toast.makeText(this, R.string.quiz_resultat_enregistre, Toast.LENGTH_SHORT).show();
    }

    private void afficherResultat(int score, int total) {
        int pourcentage = total == 0 ? 0 : Math.round(score * 100f / total);
        texteResultat.setText(getString(R.string.quiz_resultat, score, total, pourcentage));
        texteResultat.setVisibility(View.VISIBLE);
    }

    private int dp(int valeur) {
        return Math.round(valeur * getResources().getDisplayMetrics().density);
    }

    private void afficherErreur(String message) {
        indicateurChargement.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
