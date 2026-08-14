package com.example.fitzone.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.R;
import com.example.fitzone.modeles.User;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;

public class InscriptionActivity extends AppCompatActivity {

    private EditText champPrenom;
    private EditText champNom;
    private EditText champCourriel;
    private EditText champMotDePasse;
    private EditText champTelephone;
    private EditText champPhoto;
    private Button boutonInscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        champPrenom = findViewById(R.id.champPrenom);
        champNom = findViewById(R.id.champNom);
        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        champTelephone = findViewById(R.id.champTelephone);
        champPhoto = findViewById(R.id.champPhoto);
        boutonInscription = findViewById(R.id.boutonInscription);

        boutonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inscrire();
            }
        });
    }

    private void inscrire() {
        String prenom = champPrenom.getText().toString().trim();
        String nom = champNom.getText().toString().trim();
        String courriel = champCourriel.getText().toString().trim();
        String motDePasse = champMotDePasse.getText().toString();
        String telephone = champTelephone.getText().toString().trim();
        String photo = champPhoto.getText().toString().trim();

        if (prenom.isEmpty() || nom.isEmpty() || courriel.isEmpty()
                || motDePasse.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, R.string.connexion_champs_vides, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!courriel.contains("@")) {
            Toast.makeText(this, R.string.inscription_courriel_invalide,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setUsername(prenom);
        user.setEmail(courriel);
        user.setPassword(motDePasse);
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setTelephone(telephone);
        user.setPhotoUrl(photo);

        boutonInscription.setEnabled(false);

        try {
            ApiClient.post("/users", user.toJson(), new ApiCallback() {
                @Override
                public void onSuccess(String body) {
                    boutonInscription.setEnabled(true);
                    Toast.makeText(InscriptionActivity.this, R.string.inscription_reussie,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String message) {
                    boutonInscription.setEnabled(true);
                    Toast.makeText(InscriptionActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            boutonInscription.setEnabled(true);
            Toast.makeText(this, R.string.inscription_echec, Toast.LENGTH_SHORT).show();
        }
    }
}
