package com.apps.quiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class QuestionActivity extends AppCompatActivity {

    private TextView question;
    private TextView scoreText;
    private TextView questionNoText;
    private Button option1;
    private Button option2;
    private Button option3;
    private Button option4;

    private ArrayList<Question> questions;
    private String category;
    private String totalQuestion;
    private int qNo;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        getSupportActionBar().hide();
        String url = getIntent().getStringExtra("URL");
        category = getIntent().getStringExtra("Category");
        totalQuestion = getIntent().getStringExtra("Questions");

        question = findViewById(R.id.question);
        scoreText = findViewById(R.id.score);
        option1 = findViewById(R.id.option_1);
        option2 = findViewById(R.id.option_2);
        option3 = findViewById(R.id.option_3);
        option4 = findViewById(R.id.option_4);

        questionNoText = findViewById(R.id.question_no);

        qNo = 0;
        score = 0;

        scoreText.setText(String.valueOf(score));

        NetworkUtils utils = new NetworkUtils(this);

        utils.getQuestions(url, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<Question> results) {
                questions = results;
                if (questions != null && questions.size() > 0) {
                    setQuestion(qNo++);
                    findViewById(R.id.loading_question).setVisibility(View.GONE);
                    findViewById(R.id.question_layout).setVisibility(View.VISIBLE);
                }

            }
        });

    }

    /**
     * This method links the data to question activity (sets the question and options in view)
     *
     * @param index position or index of question to set
     */
    private void setQuestion(int index) {
        Question q = questions.get(index);
        String answer = q.getAnswer();

        ArrayList<String> options = q.getOptions();
        Collections.shuffle(options);

        String q_no = (qNo) + "/" + totalQuestion;
        questionNoText.setText(q_no);

        question.setText(q.getQuestion());

        option1.setText(options.get(0));
        option2.setText(options.get(1));
        if (options.size() > 2) {
            option3.setText(options.get(2));
            option4.setText(options.get(3));
            option3.setVisibility(View.VISIBLE);
            option4.setVisibility(View.VISIBLE);
        } else {
            option3.setVisibility(View.GONE);
            option4.setVisibility(View.GONE);
        }

        setClickListener(option1, options, answer, 0);
        setClickListener(option2, options, answer, 1);
        setClickListener(option3, options, answer, 2);
        setClickListener(option4, options, answer, 3);
    }

    /**
     * Click listener for option buttons
     *
     * @param button  option button
     * @param options all option buttons
     * @param answer  correct answer text
     * @param index   position of correct answer in options
     */
    private void setClickListener(Button button, ArrayList<String> options, String answer, int index) {
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer.equals(options.get(index))) {

                    button.setBackgroundColor(getResources().getColor(R.color.correct));
                    score += 10;
                    scoreText.setText(String.valueOf(score));
                } else {
                    findCorrectAnswer(answer, options);
                    button.setBackgroundColor(getResources().getColor(R.color.wrong));
                }
                stopClickable();

                nextQuestion();
            }
        });
    }

    /**
     * This method is for transition to next question with a delay of 2000ms
     */
    private void nextQuestion() {
        int delay = 2000;
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (qNo < questions.size()) {
                    setQuestion(qNo++);
                } else {
                    showScore();
                }
            }
        }, delay);
    }

    /**
     * This method displays final score statistics
     */
    private void showScore() {
        ConstraintLayout question = findViewById(R.id.question_layout);
        question.setVisibility(View.GONE);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
        Date date = new Date();

        SharedPreferences sharedPref = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);
        String[] scores = sharedPref.getString(category, "").split(";");
        int highScore = Integer.parseInt(scores[0]);
        if (highScore < score) {
            sharedPref.edit().putString(category, score + ";" + formatter.format(date)).apply();
        }


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim);

        TextView categoryText = findViewById(R.id.category_result);
        categoryText.setText(category);

        TextView scoreText = findViewById(R.id.final_score);
        scoreText.setText(String.valueOf(score));

        TextView highscoreText = findViewById(R.id.high_score);
        highscoreText.setText(String.valueOf(highScore));

        String colon = ":  ";
        TextView attempted = findViewById(R.id.attempted);
        attempted.setText(String.format("%s%s", colon, totalQuestion));

        int right = score / 10;
        TextView correct = findViewById(R.id.correct);
        correct.setText(String.format("%s%s", colon, right));

        int wrong = Integer.parseInt(totalQuestion) - right;
        TextView incorrect = findViewById(R.id.incorrect);
        incorrect.setText(String.format("%s%s", colon, wrong));

        ConstraintLayout result = findViewById(R.id.result_layout);
        result.setVisibility(View.VISIBLE);

        result.startAnimation(animation);
    }

    /**
     * This method to stop options click listeners during transition of questions
     */
    private void stopClickable() {
        option1.setClickable(false);
        option2.setClickable(false);
        option3.setClickable(false);
        option4.setClickable(false);
    }

    /**
     * This method sets the correct color for the options after answer selections
     *
     * @param answer  correct answer text
     * @param options all options for the question
     */
    private void findCorrectAnswer(String answer, ArrayList<String> options) {
        int index = options.indexOf(answer);

        switch (index) {
            case 0:
                option1.setBackgroundColor(getResources().getColor(R.color.correct));
                break;
            case 1:
                option2.setBackgroundColor(getResources().getColor(R.color.correct));
                break;
            case 2:
                option3.setBackgroundColor(getResources().getColor(R.color.correct));
                break;
            case 3:
                option4.setBackgroundColor(getResources().getColor(R.color.correct));
        }
    }

}