package com.apps.quiz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.ScoreViewHolder> {
    private final ArrayList<HighScore> highScores;

    public HighScoreAdapter(ArrayList<HighScore> highScores) {
        this.highScores = highScores;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highscore, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (highScores == null) return 0;
        return highScores.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        HighScore score = highScores.get(position);
        holder.getCategory().setText(score.getCategory());
        holder.getScore().setText(String.valueOf(score.getScore()));
        holder.getDate().setText(score.getDate());
    }

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {
        private final TextView category;
        private final TextView score;
        private final TextView date;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.score_category);
            score = itemView.findViewById(R.id.score_score);
            date = itemView.findViewById(R.id.score_date);
        }

        public TextView getCategory() {
            return category;
        }

        public TextView getScore() {
            return score;
        }

        public TextView getDate() {
            return date;
        }
    }
}
