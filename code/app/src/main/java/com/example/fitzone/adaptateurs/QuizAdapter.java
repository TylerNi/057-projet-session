package com.example.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.R;
import com.example.fitzone.modeles.Quiz;
import com.example.fitzone.modeles.QuizResult;

import java.util.List;
import java.util.Map;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    public interface SurClicQuiz {
        void ouvrir(Quiz quiz);
    }

    private final List<Quiz> quiz;
    private final Map<String, QuizResult> resultats;
    private final SurClicQuiz surClic;

    public QuizAdapter(List<Quiz> quiz, Map<String, QuizResult> resultats, SurClicQuiz surClic) {
        this.quiz = quiz;
        this.resultats = resultats;
        this.surClic = surClic;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vue = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(vue);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz item = quiz.get(position);
        QuizResult resultat = resultats.get(item.getId());

        holder.texteTitre.setText(item.getTitle());
        holder.texteQuestions.setText(holder.itemView.getContext().getString(
                R.string.quiz_questions, item.getQuestions().size()));
        holder.texteStatut.setBackgroundResource(R.drawable.bg_status_pill);

        if (resultat == null) {
            holder.texteStatut.setText(R.string.quiz_non_commence);
            holder.texteStatut.setTextColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.statut_a_faire));
            holder.texteScore.setVisibility(View.GONE);
        } else {
            holder.texteStatut.setText(R.string.quiz_termine);
            holder.texteStatut.setTextColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.statut_validee));
            holder.texteScore.setText(holder.itemView.getContext().getString(
                    R.string.quiz_score_court, resultat.getScore(), resultat.getTotal()));
            holder.texteScore.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> surClic.ouvrir(item));
    }

    @Override
    public int getItemCount() {
        return quiz.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        final TextView texteTitre;
        final TextView texteQuestions;
        final TextView texteStatut;
        final TextView texteScore;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            texteTitre = itemView.findViewById(R.id.texteTitreQuiz);
            texteQuestions = itemView.findViewById(R.id.texteNombreQuestions);
            texteStatut = itemView.findViewById(R.id.texteStatutQuiz);
            texteScore = itemView.findViewById(R.id.texteScoreQuiz);
        }
    }
}
