package com.example.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.R;
import com.example.fitzone.dao.UtilisateurDao;
import com.example.fitzone.modeles.User;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.SessionManager;

import org.json.JSONArray;

import java.net.URLEncoder;

public class ConnexionActivity extends AppCompatActivity {

    private EditText champCourriel;
    private EditText champMotDePasse;
    private Button boutonConnexion;
    private TextView lienInscription;

    private SessionManager session;
    private UtilisateurDao utilisateurDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        boutonConnexion = findViewById(R.id.boutonConnexion);
        lienInscription = findViewById(R.id.lienInscription);

        session = new SessionManager(this);
        utilisateurDao = new UtilisateurDao(this);

        boutonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tenterConnexion();
            }
        });

        lienInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConnexionActivity.this, InscriptionActivity.class));
            }
        });
    }

    private void tenterConnexion() {
        String courriel = champCourriel.getText().toString().trim();
        String motDePasse = champMotDePasse.getText().toString();

        if (courriel.isEmpty() || motDePasse.isEmpty()) {
            afficherMessage(getString(R.string.connexion_champs_vides));
            return;
        }

        boutonConnexion.setEnabled(false);

        String chemin;
        try {
            chemin = "/users?email=" + URLEncoder.encode(courriel, "UTF-8");
        } catch (Exception e) {
            boutonConnexion.setEnabled(true);
            afficherMessage(getString(R.string.connexion_identifiants_invalides));
            return;
        }

        ApiClient.get(chemin, new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                boutonConnexion.setEnabled(true);
                validerReponse(body, motDePasse);
            }

            @Override
            public void onError(String message) {
                boutonConnexion.setEnabled(true);
                afficherMessage(message);
            }
        });
    }

    private void validerReponse(String body, String motDePasse) {
        try {
            JSONArray resultats = new JSONArray(body);
            if (resultats.length() == 0) {
                afficherMessage(getString(R.string.connexion_identifiants_invalides));
                return;
            }

            User user = new User(resultats.getJSONObject(0));
            if (!motDePasse.equals(user.getPassword())) {
                afficherMessage(getString(R.string.connexion_identifiants_invalides));
                return;
            }

            session.ouvrirSession(user.getId());
            utilisateurDao.sauvegarder(user);

            startActivity(new Intent(this, AccueilActivity.class));
            finish();
        } catch (Exception e) {
            afficherMessage(getString(R.string.connexion_identifiants_invalides));
        }
    }

    private void afficherMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
