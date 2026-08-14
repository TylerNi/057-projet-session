package com.example.fitzone.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.adaptateurs.AlimentAdapter;
import com.example.fitzone.modeles.Aliment;
import com.example.fitzone.reseau.ApiCallback;
import com.example.fitzone.reseau.ApiClient;
import com.example.fitzone.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class NutritionActivity extends AppCompatActivity {

    private RecyclerView listeNutrition;
    private TextView texteVide;
    private LinearProgressIndicator indicateurChargement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        listeNutrition = findViewById(R.id.listeNutrition);
        texteVide = findViewById(R.id.texteVide);
        indicateurChargement = findViewById(R.id.indicateurChargement);
        listeNutrition.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        NavigationHelper.configurer(this, navigation, R.id.navNutrition);

        chargerAliments();
    }

    private void chargerAliments() {
        ApiClient.get("/nutrition", new ApiCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray tableau = new JSONArray(body);
                    List<Aliment> aliments = new ArrayList<>();
                    for (int i = 0; i < tableau.length(); i++) {
                        aliments.add(new Aliment(tableau.getJSONObject(i)));
                    }

                    indicateurChargement.setVisibility(View.GONE);
                    texteVide.setVisibility(aliments.isEmpty() ? View.VISIBLE : View.GONE);
                    listeNutrition.setAdapter(new AlimentAdapter(aliments));
                } catch (Exception e) {
                    indicateurChargement.setVisibility(View.GONE);
                    Toast.makeText(NutritionActivity.this, R.string.erreur_chargement,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                indicateurChargement.setVisibility(View.GONE);
                Toast.makeText(NutritionActivity.this, message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
