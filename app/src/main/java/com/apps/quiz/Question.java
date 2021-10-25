package com.apps.quiz;

import java.util.ArrayList;

public class Question {
    private final String question;
    private final String answer;
    private final ArrayList<String> options;

    public Question(String question, ArrayList<String> options, String answer) {
        this.question = question;
        this.answer = answer;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}
