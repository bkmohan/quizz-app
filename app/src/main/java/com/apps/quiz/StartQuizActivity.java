package com.apps.quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartQuizActivity extends AppCompatActivity {

    private Spinner category;
    private Spinner type;
    private Spinner noQuestions;

    private static final String BASE_URL = "https://opentdb.com/api.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initSpinners();
        initScorePreferences();

        Button start = findViewById(R.id.start_quiz);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] urlData = generateURL();

                Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                intent.putExtra("URL", urlData[0]);
                intent.putExtra("Category", urlData[1]);
                intent.putExtra("Questions", urlData[2]);
                startActivity(intent);
            }
        });

        Button whatsapp = findViewById(R.id.whatapp);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + "9886679599");
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);

                String text = "YOUR TEXT HERE";
                i.putExtra(Intent.EXTRA_TEXT, text);
                i.setPackage("com.whatsapp");
                
                startActivity(i);

                /*
                PackageInfo info = null;
                try {
                    info = getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                */

            }
        });
    }

    private void initSpinners() {
        category = findViewById(R.id.category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, categoryText);
        category.setAdapter(categoryAdapter);

        type = findViewById(R.id.type);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, typeTest);
        type.setAdapter(typeAdapter);

        noQuestions = findViewById(R.id.no_questions);
        ArrayAdapter<String> questionsAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, noQ);
        noQuestions.setAdapter(questionsAdapter);
    }

    private String[] generateURL() {
        String queryCategory = categoryValues[category.getSelectedItemPosition()];
        String queryType = type.getSelectedItem().toString();
        String queryNoQ = noQuestions.getSelectedItem().toString();

        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("amount", queryNoQ);
        if (!queryCategory.equals("0")) uriBuilder.appendQueryParameter("category", queryCategory);
        if (queryType.equals("Multiple Choice"))
            uriBuilder.appendQueryParameter("type", "multiple");
        else if (queryType.equals("True/False")) uriBuilder.appendQueryParameter("type", "boolean");

        return new String[]{uriBuilder.build().toString(), categoryText[category.getSelectedItemPosition()], queryNoQ};
    }

    /**
     * This methods initializes the high scores for the first time
     */
    private void initScorePreferences() {
        SharedPreferences sharedPref = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        for (String category : categoryText) {
            if (!sharedPref.contains(category)) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
                Date date = new Date();
                editor.putString(category, "0;" + formatter.format(date));
            }
        }
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_high_score) {
            Intent intent = new Intent(this, HighscoreActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final String[] categoryText = {"Any Category", "General Knowledge", "Entertainment: Books", "Entertainment: Film", "Entertainment: Music",
            "Entertainment: Television", "Entertainment: Video Games", "Entertainment: Board Games", "Science: Nature", "Science: Computers",
            "Science: Mathematics", "Mythology", "Sports", "Geography", "History", "Politics", "Celebrities", "Animals", "Vehicles",
            "Entertainment: Comics", "Entertainment: Japanese Anime & Manga", "Entertainment: Cartoon & Animations"};

    private final String[] categoryValues = {"0", "9", "10", "11", "12", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "26", "27", "28", "29", "31", "32"};

    private final String[] typeTest = {"Any Type", "Multiple Choice", "True/False"};
    private final String[] noQ = {"10", "20", "30", "40", "50"};

}