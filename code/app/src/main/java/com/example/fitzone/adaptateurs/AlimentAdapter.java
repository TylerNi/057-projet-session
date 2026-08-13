package com.example.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.modeles.Aliment;
import java.util.List;

public class AlimentAdapter extends RecyclerView.Adapter<AlimentAdapter.AlimentViewHolder> {

    private final List<Aliment> aliments;

    public AlimentAdapter(List<Aliment> aliments) {
        this.aliments = aliments;
    }

    @NonNull
    @Override
    public AlimentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vue = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aliment, parent, false);
        return new AlimentViewHolder(vue);
    }

    @Override
    public void onBindViewHolder(@NonNull AlimentViewHolder holder, int position) {
        Aliment aliment = aliments.get(position);
        holder.image.setImageResource(imagePour(aliment.getImage()));
        holder.image.setContentDescription(aliment.getNom());
        holder.nom.setText(aliment.getNom());
        holder.description.setText(aliment.getDescription());
        holder.calories.setText(holder.itemView.getContext().getString(
                R.string.nutrition_calories, aliment.getCalories()));
        holder.moment.setText(holder.itemView.getContext().getString(
                R.string.nutrition_moment, aliment.getMoment()));
    }

    @Override
    public int getItemCount() {
        return aliments.size();
    }

    private int imagePour(String image) {
        if ("nutrition_banane".equals(image)) return R.drawable.nutrition_banane;
        if ("nutrition_amandes".equals(image)) return R.drawable.nutrition_amandes;
        if ("nutrition_oeufs".equals(image)) return R.drawable.nutrition_oeufs;
        if ("nutrition_poulet".equals(image)) return R.drawable.nutrition_poulet;
        if ("nutrition_yogourt".equals(image)) return R.drawable.nutrition_yogourt;
        if ("nutrition_pomme".equals(image)) return R.drawable.nutrition_pomme;
        if ("nutrition_quinoa".equals(image)) return R.drawable.nutrition_quinoa;
        if ("nutrition_saumon".equals(image)) return R.drawable.nutrition_saumon;
        if ("nutrition_avocat".equals(image)) return R.drawable.nutrition_avocat;
        return R.drawable.nutrition_gruau;
    }

    static class AlimentViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView nom;
        final TextView description;
        final TextView calories;
        final TextView moment;

        AlimentViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageAliment);
            nom = itemView.findViewById(R.id.texteNomAliment);
            description = itemView.findViewById(R.id.texteDescriptionAliment);
            calories = itemView.findViewById(R.id.texteCaloriesAliment);
            moment = itemView.findViewById(R.id.texteMomentAliment);
        }
    }
}
