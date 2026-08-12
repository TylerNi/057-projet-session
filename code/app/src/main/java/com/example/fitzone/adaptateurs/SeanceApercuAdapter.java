package com.example.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.modeles.Seance;

import java.util.List;
import java.util.Map;

public class SeanceApercuAdapter extends RecyclerView.Adapter<SeanceApercuAdapter.SeanceViewHolder> {

    private List<Seance> seances;
    private Map<String, String> statuts;

    public SeanceApercuAdapter(List<Seance> seances, Map<String, String> statuts) {
        this.seances = seances;
        this.statuts = statuts;
    }

    @NonNull
    @Override
    public SeanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vue = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seance_apercu, parent, false);
        return new SeanceViewHolder(vue);
    }

    @Override
    public void onBindViewHolder(@NonNull SeanceViewHolder holder, int position) {
        Seance seance = seances.get(position);
        holder.texteTitreSeance.setText(seance.getTitle());
        holder.texteEcheance.setText(holder.itemView.getContext()
                .getString(R.string.accueil_echeance, seance.getDueDate()));
        holder.texteStatutSeance.setText(statuts.get(seance.getId()));
    }

    @Override
    public int getItemCount() {
        return seances.size();
    }

    static class SeanceViewHolder extends RecyclerView.ViewHolder {

        TextView texteTitreSeance;
        TextView texteEcheance;
        TextView texteStatutSeance;

        SeanceViewHolder(View vue) {
            super(vue);
            texteTitreSeance = vue.findViewById(R.id.texteTitreSeance);
            texteEcheance = vue.findViewById(R.id.texteEcheance);
            texteStatutSeance = vue.findViewById(R.id.texteStatutSeance);
        }
    }
}
