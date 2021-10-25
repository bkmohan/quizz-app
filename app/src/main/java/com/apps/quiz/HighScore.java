package com.apps.quiz;

import java.util.Comparator;

public class HighScore {

    private final String category;
    private final int score;
    private final String date;

    public HighScore(String category, int score, String date) {
        this.category = category;
        this.score = score;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public int getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }
}

/**
 * A Comparator class to compare high scores
 */
class ScoreComparator implements Comparator<HighScore> {
    @Override
    public int compare(HighScore o1, HighScore o2) {
        return o2.getScore() - o1.getScore();
    }
}

