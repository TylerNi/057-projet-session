package com.example.fitzone.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.fitzone.R;
import com.example.fitzone.dao.EtatSeanceDao;
import com.example.fitzone.modeles.Seance;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.SessionManager;
import com.example.fitzone.utils.StatutSeance;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailSeanceActivity extends AppCompatActivity {

    public static final String EXTRA_SEANCE_ID = "seanceId";

    private String seanceId;
    private String userId;
    private Seance seance;
    private EtatSeanceDao etatSeanceDao;

    private LinearProgressIndicator indicateurChargement;
    private View contenuSeance;
    private TextView texteTitre;
    private TextView texteEcheance;
    private TextView texteStatut;
    private TextView texteDateSoumission;
    private TextView texteDescription;
    private TextView texteConsignes;
    private TextView texteNote;
    private TextView texteCommentaire;
    private TextView texteSoumissionVerrouillee;
    private TextInputLayout conteneurSoumission;
    private TextInputEditText champSoumission;
    private MaterialButton boutonSoumettre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_seance);

        seanceId = getIntent().getStringExtra(EXTRA_SEANCE_ID);
        userId = new SessionManager(this).obtenirUserId();
        if (seanceId == null || userId == null) {
            finish();
            return;
        }

        MaterialToolbar barreOutils = findViewById(R.id.barreOutils);
        barreOutils.setNavigationOnClickListener(v -> finish());

        indicateurChargement = findViewById(R.id.indicateurChargement);
        contenuSeance = findViewById(R.id.contenuSeance);
        texteTitre = findViewById(R.id.texteTitreSeance);
        texteEcheance = findViewById(R.id.texteEcheanceSeance);
        texteStatut = findViewById(R.id.texteStatutSeance);
        texteDateSoumission = findViewById(R.id.texteDateSoumission);
        texteDescription = findViewById(R.id.texteDescriptionSeance);
        texteConsignes = findViewById(R.id.texteConsignesSeance);
        texteNote = findViewById(R.id.texteNoteSeance);
        texteCommentaire = findViewById(R.id.texteCommentaireSeance);
        texteSoumissionVerrouillee = findViewById(R.id.texteSoumissionVerrouillee);
        conteneurSoumission = findViewById(R.id.conteneurSoumission);
        champSoumission = findViewById(R.id.champSoumission);
        boutonSoumettre = findViewById(R.id.boutonSoumettre);

        etatSeanceDao = new EtatSeanceDao(this);
        boutonSoumettre.setOnClickListener(v -> enregistrerSoumission());

        chargerSeance();
    }

    private void chargerSeance() {
        ApiClient.get("/seances/" + seanceId, new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    seance = new Seance(new JSONObject(body));
                    afficherSeance();
                } catch (Exception e) {
                    indicateurChargement.setVisibility(View.GONE);
                    Toast.makeText(DetailSeanceActivity.this, R.string.seance_introuvable,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                indicateurChargement.setVisibility(View.GONE);
                Toast.makeText(DetailSeanceActivity.this, message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void afficherSeance() {
        indicateurChargement.setVisibility(View.GONE);
        contenuSeance.setVisibility(View.VISIBLE);

        texteTitre.setText(seance.getTitle());
        texteEcheance.setText(getString(R.string.seance_echeance, seance.getDueDate()));
        texteDescription.setText(seance.getDescription());
        texteConsignes.setText(seance.getInstructions());

        if (seance.getGrade() == null) {
            texteNote.setText(R.string.seance_note_aucune);
        } else {
            texteNote.setText(getString(R.string.seance_note,
                    seance.getGrade() + "/" + seance.getTotalPoints()));
        }
        if (seance.getComment() == null || seance.getComment().trim().isEmpty()) {
            texteCommentaire.setText(R.string.seance_commentaire_aucun);
        } else {
            texteCommentaire.setText(getString(R.string.seance_commentaire, seance.getComment()));
        }

        String statutLocal = etatSeanceDao.obtenirStatut(userId, seanceId);
        String statut = StatutSeance.calculer(seance, statutLocal);
        texteStatut.setText(statut);
        texteStatut.setTextColor(ContextCompat.getColor(this, StatutSeance.couleur(statut)));

        String dateSoumission = etatSeanceDao.obtenirDateSoumission(userId, seanceId);
        if (dateSoumission == null || dateSoumission.isEmpty()) {
            texteDateSoumission.setVisibility(View.GONE);
        } else {
            texteDateSoumission.setText(getString(R.string.seance_soumise_le, dateSoumission));
            texteDateSoumission.setVisibility(View.VISIBLE);
        }

        String contenu = etatSeanceDao.obtenirContenu(userId, seanceId);
        if (contenu != null && champSoumission.getText() != null
                && champSoumission.getText().length() == 0) {
            champSoumission.setText(contenu);
        }

        boolean estDisponible = StatutSeance.estDisponible(seance);
        boolean estValidee = StatutSeance.VALIDEE.equals(statut);
        texteSoumissionVerrouillee.setVisibility(estDisponible ? View.GONE : View.VISIBLE);
        if (!estDisponible) {
            texteSoumissionVerrouillee.setText(getString(
                    R.string.seance_disponible_le, seance.getAvailableDate()));
        }
        conteneurSoumission.setVisibility(estDisponible ? View.VISIBLE : View.GONE);
        conteneurSoumission.setEnabled(!estValidee);
        champSoumission.setEnabled(!estValidee);
        boutonSoumettre.setVisibility(estDisponible && !estValidee ? View.VISIBLE : View.GONE);
        boutonSoumettre.setText(StatutSeance.SOUMISE.equals(statut)
                ? R.string.seance_mettre_a_jour : R.string.seance_marquer_soumise);
    }

    private void enregistrerSoumission() {
        if (seance == null || !StatutSeance.estDisponible(seance)) {
            Toast.makeText(this, R.string.seance_soumission_indisponible,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String contenu = champSoumission.getText() == null
                ? "" : champSoumission.getText().toString().trim();
        if (contenu.isEmpty()) {
            conteneurSoumission.setError(getString(R.string.seance_contenu_requis));
            return;
        }
        conteneurSoumission.setError(null);

        String maintenant = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA).format(new Date());
        etatSeanceDao.enregistrer(userId, seanceId, StatutSeance.SOUMISE, maintenant, contenu);
        Toast.makeText(this, R.string.seance_soumission_reussie, Toast.LENGTH_SHORT).show();
        afficherSeance();
    }

}
