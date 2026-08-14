package com.example.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.R;
import com.example.fitzone.dao.UtilisateurDao;
import com.example.fitzone.modeles.Program;
import com.example.fitzone.modeles.Seance;
import com.example.fitzone.modeles.User;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.ImageLoader;
import com.example.fitzone.utils.NavigationHelper;
import com.example.fitzone.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfilActivity extends AppCompatActivity {

    private String userId;
    private User utilisateurServeur;
    private User utilisateurAffiche;
    private UtilisateurDao utilisateurDao;
    private SessionManager sessionManager;

    private LinearProgressIndicator indicateurChargement;
    private ImageView imageProfil;
    private View carteResume;
    private View carteEdition;
    private TextView texteNomComplet;
    private TextView texteCourrielResume;
    private TextView texteTelephoneResume;
    private TextView texteSeancesReussies;
    private TextView texteProgrammesTermines;
    private TextView texteNoteMoyenne;
    private TextInputLayout conteneurPrenom;
    private TextInputLayout conteneurNom;
    private TextInputLayout conteneurTelephone;
    private TextInputEditText champPrenom;
    private TextInputEditText champNom;
    private TextInputEditText champCourriel;
    private TextInputEditText champTelephone;
    private TextInputEditText champPhoto;
    private TextInputEditText champMotDePasse;
    private MaterialButton boutonModifier;
    private MaterialButton boutonDeconnexion;
    private MaterialButton boutonEnregistrer;
    private MaterialButton boutonAnnuler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        sessionManager = new SessionManager(this);
        userId = sessionManager.obtenirUserId();
        if (userId == null) {
            Toast.makeText(this, R.string.profil_session_invalide, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        indicateurChargement = findViewById(R.id.indicateurChargement);
        imageProfil = findViewById(R.id.imageProfil);
        carteResume = findViewById(R.id.carteResumeProfil);
        carteEdition = findViewById(R.id.carteEditionProfil);
        texteNomComplet = findViewById(R.id.texteNomCompletProfil);
        texteCourrielResume = findViewById(R.id.texteCourrielResumeProfil);
        texteTelephoneResume = findViewById(R.id.texteTelephoneResumeProfil);
        texteSeancesReussies = findViewById(R.id.texteSeancesReussiesProfil);
        texteProgrammesTermines = findViewById(R.id.texteProgrammesTerminesProfil);
        texteNoteMoyenne = findViewById(R.id.texteNoteMoyenneProfil);
        conteneurPrenom = findViewById(R.id.conteneurPrenom);
        conteneurNom = findViewById(R.id.conteneurNom);
        conteneurTelephone = findViewById(R.id.conteneurTelephone);
        champPrenom = findViewById(R.id.champPrenomProfil);
        champNom = findViewById(R.id.champNomProfil);
        champCourriel = findViewById(R.id.champCourrielProfil);
        champTelephone = findViewById(R.id.champTelephoneProfil);
        champPhoto = findViewById(R.id.champPhotoProfil);
        champMotDePasse = findViewById(R.id.champMotDePasseProfil);
        boutonModifier = findViewById(R.id.boutonModifierProfil);
        boutonDeconnexion = findViewById(R.id.boutonDeconnexionProfil);
        boutonEnregistrer = findViewById(R.id.boutonEnregistrerProfil);
        boutonAnnuler = findViewById(R.id.boutonAnnulerProfil);
        boutonModifier.setEnabled(false);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        NavigationHelper.configurer(this, navigation, R.id.navProfil);

        utilisateurDao = new UtilisateurDao(this);
        User cache = utilisateurDao.obtenir(userId);
        if (cache != null) {
            remplirFormulaire(cache);
            boutonModifier.setEnabled(true);
        }

        champPhoto.setOnFocusChangeListener((v, aLeFocus) -> {
            if (!aLeFocus) {
                ImageLoader.charger(texte(champPhoto), imageProfil);
            }
        });
        boutonModifier.setOnClickListener(v -> afficherModeEdition(true));
        boutonDeconnexion.setOnClickListener(v -> deconnecter());
        boutonEnregistrer.setOnClickListener(v -> enregistrerProfil());
        boutonAnnuler.setOnClickListener(v -> annulerEdition());

        afficherModeEdition(false);

        chargerProfilServeur();
    }

    private void chargerProfilServeur() {
        indicateurChargement.setVisibility(View.VISIBLE);
        ApiClient.get("/users/" + userId, new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    utilisateurServeur = new User(new JSONObject(body));
                    remplirFormulaire(utilisateurServeur);
                    utilisateurDao.sauvegarder(utilisateurServeur);
                    chargerStatistiques(utilisateurServeur);
                    indicateurChargement.setVisibility(View.GONE);
                    boutonModifier.setEnabled(true);
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

    private void remplirFormulaire(User user) {
        utilisateurAffiche = user;
        champPrenom.setText(user.getPrenom());
        champNom.setText(user.getNom());
        champCourriel.setText(user.getEmail());
        champTelephone.setText(user.getTelephone());
        champPhoto.setText(user.getPhotoUrl());
        texteNomComplet.setText((user.getPrenom() + " " + user.getNom()).trim());
        texteCourrielResume.setText(user.getEmail());
        texteTelephoneResume.setText(user.getTelephone());
        ImageLoader.charger(user.getPhotoUrl(), imageProfil);
    }

    private void chargerStatistiques(User user) {
        List<String> idsProgrammes = new ArrayList<>(user.getEnrolledProgramIds());
        chargerStatistiquesSeances(idsProgrammes);
        ApiClient.get("/programs", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    int programmesTermines = 0;
                    JSONArray tableau = new JSONArray(body);
                    for (int i = 0; i < tableau.length(); i++) {
                        Program programme = new Program(tableau.getJSONObject(i));
                        if (idsProgrammes.contains(programme.getId())
                                && "termine".equalsIgnoreCase(programme.getStatut())) {
                            programmesTermines++;
                        }
                    }
                    texteProgrammesTermines.setText(getString(
                            R.string.profil_stat_nombre, programmesTermines));
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError(String message) {
            }
        });
    }

    private void chargerStatistiquesSeances(List<String> idsProgrammes) {
        ApiClient.get("/seances", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    int seancesReussies = 0;
                    int pointsObtenus = 0;
                    int pointsPossibles = 0;
                    JSONArray tableau = new JSONArray(body);
                    for (int i = 0; i < tableau.length(); i++) {
                        Seance seance = new Seance(tableau.getJSONObject(i));
                        if (idsProgrammes.contains(seance.getProgramId())
                                && seance.getGrade() != null) {
                            seancesReussies++;
                            pointsObtenus += seance.getGrade();
                            pointsPossibles += seance.getTotalPoints();
                        }
                    }

                    texteSeancesReussies.setText(getString(
                            R.string.profil_stat_nombre, seancesReussies));
                    if (pointsPossibles > 0) {
                        int moyenne = Math.round(pointsObtenus * 100f / pointsPossibles);
                        texteNoteMoyenne.setText(getString(
                                R.string.profil_stat_pourcentage, moyenne));
                    } else {
                        texteNoteMoyenne.setText("—");
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError(String message) {
            }
        });
    }

    private void afficherModeEdition(boolean edition) {
        if (edition) {
            effacerErreurs();
        }
        carteResume.setVisibility(edition ? View.GONE : View.VISIBLE);
        boutonModifier.setVisibility(edition ? View.GONE : View.VISIBLE);
        boutonDeconnexion.setVisibility(edition ? View.GONE : View.VISIBLE);
        carteEdition.setVisibility(edition ? View.VISIBLE : View.GONE);
    }

    private void deconnecter() {
        sessionManager.fermerSession();
        Intent intent = new Intent(this, ConnexionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void annulerEdition() {
        champMotDePasse.setText("");
        effacerErreurs();
        if (utilisateurAffiche != null) {
            remplirFormulaire(utilisateurAffiche);
        }
        afficherModeEdition(false);
    }

    private void effacerErreurs() {
        conteneurPrenom.setError(null);
        conteneurNom.setError(null);
        conteneurTelephone.setError(null);
    }

    private void enregistrerProfil() {
        String prenom = texte(champPrenom);
        String nom = texte(champNom);
        String telephone = texte(champTelephone);
        if (prenom.isEmpty() || nom.isEmpty() || telephone.isEmpty()) {
            validerChamps(prenom, nom, telephone);
            Toast.makeText(this, R.string.profil_champs_requis, Toast.LENGTH_SHORT).show();
            return;
        }
        validerChamps(prenom, nom, telephone);

        boutonEnregistrer.setEnabled(false);
        indicateurChargement.setVisibility(View.VISIBLE);

        ApiClient.get("/users/" + userId, new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    User frais = new User(new JSONObject(body));
                    frais.setPrenom(prenom);
                    frais.setNom(nom);
                    frais.setTelephone(telephone);
                    frais.setPhotoUrl(texte(champPhoto));
                    String nouveauMotDePasse = texte(champMotDePasse);
                    if (!nouveauMotDePasse.isEmpty()) {
                        frais.setPassword(nouveauMotDePasse);
                    }
                    envoyerProfil(frais);
                } catch (Exception e) {
                    terminerEnErreur(getString(R.string.erreur_chargement));
                }
            }

            @Override
            public void onError(String message) {
                terminerEnErreur(message);
            }
        });
    }

    private void envoyerProfil(User user) {
        try {
            ApiClient.put("/users/" + userId, user.toJson(), new ApiCallback() {
                @Override
                public void onSuccess(String body) {
                    try {
                        utilisateurServeur = body == null || body.isEmpty()
                                ? user : new User(new JSONObject(body));
                        utilisateurDao.sauvegarder(utilisateurServeur);
                        champMotDePasse.setText("");
                        remplirFormulaire(utilisateurServeur);
                        indicateurChargement.setVisibility(View.GONE);
                        boutonEnregistrer.setEnabled(true);
                        afficherModeEdition(false);
                        Toast.makeText(ProfilActivity.this,
                                R.string.profil_enregistre, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        terminerEnErreur(getString(R.string.erreur_chargement));
                    }
                }

                @Override
                public void onError(String message) {
                    terminerEnErreur(message);
                }
            });
        } catch (Exception e) {
            terminerEnErreur(getString(R.string.erreur_chargement));
        }
    }

    private void validerChamps(String prenom, String nom, String telephone) {
        conteneurPrenom.setError(prenom.isEmpty() ? getString(R.string.profil_prenom) : null);
        conteneurNom.setError(nom.isEmpty() ? getString(R.string.profil_nom) : null);
        conteneurTelephone.setError(telephone.isEmpty() ? getString(R.string.profil_telephone) : null);
    }

    private String texte(TextInputEditText champ) {
        return champ.getText() == null ? "" : champ.getText().toString().trim();
    }

    private void afficherErreur(String message) {
        indicateurChargement.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void terminerEnErreur(String message) {
        indicateurChargement.setVisibility(View.GONE);
        boutonEnregistrer.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
