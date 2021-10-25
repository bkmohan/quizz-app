package com.apps.quiz;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static RequestQueue queue;
    private static final String HELPER_URL = "https://opentdb.com/api_token.php?command=";
    private static String token = "";

    public NetworkUtils(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void getQuestions(String url, final VolleyCallback callback) {
        if (token.isEmpty()) {
            newSession(url, callback);
        } else {
            continueSession(url, callback);
        }
    }

    /**
     * This method gets new token from the api to form a new session
     *
     * @param url      url to request new session token
     * @param callback callback listener to respond back after successful network response
     */
    private void newSession(String url, final VolleyCallback callback) {
        String tokenURL = HELPER_URL + "request";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, tokenURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            token = response.getString("token");
                            continueSession(url, callback);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error getting session token\n" + error.getMessage());
            }
        });

        queue.add(jsonRequest);
    }

    /**
     * This method continues wth the session/token from the new session,
     * so that the questions are not repeated in same session
     *
     * @param url      url to request question data
     * @param callback callback listener to respond back after successful network response
     */
    private void continueSession(String url, final VolleyCallback callback) {
        String finalURL = url + "&token=" + token;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, finalURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("response_code");
                            if (code == 0) {
                                ArrayList<Question> questions = new ArrayList<>();
                                try {
                                    JSONArray results = response.getJSONArray("results");

                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject result = results.getJSONObject(i);
                                        String question = Html.fromHtml(result.getString("question")).toString();
                                        String answer = Html.fromHtml(result.getString("correct_answer")).toString();

                                        ArrayList<String> options = new ArrayList<>();
                                        options.add(answer);
                                        JSONArray optionArray = result.getJSONArray("incorrect_answers");
                                        for (int j = 0; j < optionArray.length(); j++) {
                                            options.add(Html.fromHtml(optionArray.getString(j)).toString());
                                        }
                                        questions.add(new Question(question, options, answer));
                                    }
                                    callback.onSuccess(questions);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (code == 4) {
                                resetSession(url, callback);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error getting session token\n" + error.getMessage());
            }
        });

        queue.add(jsonRequest);
    }

    /**
     * This method resets token, if the there are no more new questions or if token has expired
     *
     * @param url      url to request reset session token
     * @param callback callback listener to respond back after successful network response
     */
    private void resetSession(String url, VolleyCallback callback) {
        String resetURL = HELPER_URL + "reset&token=" + token;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, resetURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            token = response.getString("token");
                            continueSession(url, callback);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error getting session token\n" + error.getMessage());
            }
        });

        queue.add(jsonRequest);
    }

}

/**
 * An interface to send back the response obtained from successful volley request
 */
interface VolleyCallback {
    void onSuccess(ArrayList<Question> questions);
}

