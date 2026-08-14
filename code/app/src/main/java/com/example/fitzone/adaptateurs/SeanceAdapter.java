package com.example.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.modeles.Seance;
import com.example.fitzone.utils.StatutSeance;

import java.util.List;
import java.util.Map;

public class SeanceAdapter extends RecyclerView.Adapter<SeanceAdapter.SeanceViewHolder> {

    public interface SurClicSeance {
        void ouvrir(Seance seance);
    }

    private final List<Seance> seances;
    private final Map<String, String> statuts;
    private final SurClicSeance surClic;

    public SeanceAdapter(List<Seance> seances, Map<String, String> statuts, SurClicSeance surClic) {
        this.seances = seances;
        this.statuts = statuts;
        this.surClic = surClic;
    }

    @NonNull
    @Override
    public SeanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vue = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seance, parent, false);
        return new SeanceViewHolder(vue);
    }

    @Override
    public void onBindViewHolder(@NonNull SeanceViewHolder holder, int position) {
        Seance seance = seances.get(position);
        String statut = statuts.get(seance.getId());

        holder.texteTitre.setText(seance.getTitle());
        if (StatutSeance.A_VENIR.equals(statut)) {
            holder.texteEcheance.setText(holder.itemView.getContext().getString(
                    R.string.seance_disponibilite, seance.getAvailableDate()));
        } else {
            holder.texteEcheance.setText(holder.itemView.getContext().getString(
                    R.string.seance_echeance, seance.getDueDate()));
        }
        holder.texteStatut.setText(statut);
        holder.texteStatut.setBackgroundResource(R.drawable.bg_status_pill);
        holder.texteStatut.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(), StatutSeance.couleur(statut)));

        if (seance.getGrade() == null) {
            holder.texteNote.setText(R.string.seance_note_aucune);
        } else {
            holder.texteNote.setText(holder.itemView.getContext().getString(
                    R.string.seance_note, seance.getGrade() + "/" + seance.getTotalPoints()));
        }

        holder.itemView.setOnClickListener(v -> surClic.ouvrir(seance));
    }

    @Override
    public int getItemCount() {
        return seances.size();
    }

    static class SeanceViewHolder extends RecyclerView.ViewHolder {
        final TextView texteTitre;
        final TextView texteEcheance;
        final TextView texteStatut;
        final TextView texteNote;

        SeanceViewHolder(@NonNull View itemView) {
            super(itemView);
            texteTitre = itemView.findViewById(R.id.texteTitreSeance);
            texteEcheance = itemView.findViewById(R.id.texteEcheance);
            texteStatut = itemView.findViewById(R.id.texteStatutSeance);
            texteNote = itemView.findViewById(R.id.texteNoteSeance);
        }
    }
}
