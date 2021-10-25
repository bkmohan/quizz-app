package com.apps.quiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class HighscoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        getSupportActionBar().setTitle("High Score");
        ArrayList<HighScore> highScores = getHighScore();

        RecyclerView recyclerView = findViewById(R.id.high_score_rcv);
        HighScoreAdapter adapter = new HighScoreAdapter(highScores);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    }

    private ArrayList<HighScore> getHighScore() {
        ArrayList<HighScore> highScores = new ArrayList<>();

        SharedPreferences sharedPref = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);
        Map<String, ?> allScore = sharedPref.getAll();

        for (Map.Entry<String, ?> scoreEntry : allScore.entrySet()) {
            String[] scores = scoreEntry.getValue().toString().split(";");
            highScores.add(new HighScore(scoreEntry.getKey(), Integer.parseInt(scores[0]), scores[1]));
        }
        Collections.sort(highScores, new ScoreComparator());
        return highScores;
    }
}