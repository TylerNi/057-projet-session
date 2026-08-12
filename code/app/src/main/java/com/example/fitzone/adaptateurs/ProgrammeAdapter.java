package com.example.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.modeles.Program;

import java.util.List;

public class ProgrammeAdapter extends RecyclerView.Adapter<ProgrammeAdapter.ProgrammeViewHolder> {

    public interface SurClicProgramme {
        void surClic(Program programme);
    }

    private List<Program> programmes;
    private SurClicProgramme ecouteur;

    public ProgrammeAdapter(List<Program> programmes, SurClicProgramme ecouteur) {
        this.programmes = programmes;
        this.ecouteur = ecouteur;
    }

    public void mettreAJour(List<Program> nouveauxProgrammes) {
        this.programmes = nouveauxProgrammes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProgrammeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vue = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_programme, parent, false);
        return new ProgrammeViewHolder(vue);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgrammeViewHolder holder, int position) {
        Program programme = programmes.get(position);
        holder.texteCode.setText(programme.getCode());
        holder.texteTitre.setText(programme.getTitle());
        holder.texteCoach.setText(holder.itemView.getContext()
                .getString(R.string.programme_coach, programme.getCoach()));
        holder.texteSession.setText(programme.getSession());
        holder.itemView.setOnClickListener(v -> ecouteur.surClic(programme));
    }

    @Override
    public int getItemCount() {
        return programmes.size();
    }

    static class ProgrammeViewHolder extends RecyclerView.ViewHolder {

        TextView texteCode;
        TextView texteTitre;
        TextView texteCoach;
        TextView texteSession;

        ProgrammeViewHolder(View vue) {
            super(vue);
            texteCode = vue.findViewById(R.id.texteCode);
            texteTitre = vue.findViewById(R.id.texteTitre);
            texteCoach = vue.findViewById(R.id.texteCoach);
            texteSession = vue.findViewById(R.id.texteSession);
        }
    }
}
