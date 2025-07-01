package com.example.quiz2.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz2.DTO.AddScoreDTO;
import com.example.quiz2.DTO.QuestionDTO;
import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;
import com.google.gson.Gson;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionPage extends AppCompatActivity {

    private TextView questionTextView;
    private Button option1, option2, option3, option4;

    private ArrayList<QuestionDTO> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    int matchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_page);

        questionTextView = findViewById(R.id.questionText);
        option1 = findViewById(R.id.answerButton1);
        option2 = findViewById(R.id.answerButton2);
        option3 = findViewById(R.id.answerButton3);
        option4 = findViewById(R.id.answerButton4);

        questions = (ArrayList<QuestionDTO>) getIntent().getSerializableExtra("questions");
        matchId = getIntent().getIntExtra("matchId", -1);

        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "No questions provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadQuestion();
    }

    private void loadQuestion() {
        QuestionDTO question = questions.get(currentQuestionIndex);

        questionTextView.setText(question.getQuestion());
        option1.setText(question.getOption1());
        option2.setText(question.getOption2());
        option3.setText(question.getOption3());
        option4.setText(question.getOption4());

        resetButtonColors();

        option1.setOnClickListener(v -> handleAnswer(1, option1));
        option2.setOnClickListener(v -> handleAnswer(2, option2));
        option3.setOnClickListener(v -> handleAnswer(3, option3));
        option4.setOnClickListener(v -> handleAnswer(4, option4));
    }

    private void handleAnswer(int selectedOption, Button selectedButton) {
        disableButtons();

        QuestionDTO current = questions.get(currentQuestionIndex);
        int correct = current.getAnswer();

        if (selectedOption == correct) {
            selectedButton.setBackgroundColor(Color.GREEN);
            score++;
        } else {
            selectedButton.setBackgroundColor(Color.RED);
            highlightCorrectOption(correct);
        }

        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                loadQuestion();
            } else {
                sendResult();
            }
        }, 1000);
    }

    private void disableButtons() {
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
    }

    private void resetButtonColors() {
        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);

        option1.setBackgroundColor(Color.LTGRAY);
        option2.setBackgroundColor(Color.LTGRAY);
        option3.setBackgroundColor(Color.LTGRAY);
        option4.setBackgroundColor(Color.LTGRAY);
    }

    private void highlightCorrectOption(int correct) {
        switch (correct) {
            case 1:
                option1.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                option2.setBackgroundColor(Color.GREEN);
                break;
            case 3:
                option3.setBackgroundColor(Color.GREEN);
                break;
            case 4:
                option4.setBackgroundColor(Color.GREEN);
                break;
        }
    }

    private void sendResult() {
        Toast.makeText(this, "Quiz finished! Your score: " + score, Toast.LENGTH_LONG).show();
        AddScoreDTO addScoreDTO = new AddScoreDTO(matchId, score);

        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", "");

            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String requestPayload = gson.toJson(addScoreDTO); // ✅ Proper JSON

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(requestPayload, JSON);

            String url = "http://" + IP.getIp() + "/addScore";

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                final String responseBodyString = response.body() != null ? response.body().string() : null;

                if (response.isSuccessful()) {
                    if (responseBodyString != null) {
                        boolean success = Boolean.parseBoolean(responseBodyString);
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(QuestionPage.this, "Score submitted successfully!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(QuestionPage.this, MainPage.class));
                            } else {
                                Toast.makeText(QuestionPage.this, "Failed to submit score: Server indicated failure.", Toast.LENGTH_LONG).show();
                                Log.e("SendResult", "Server responded with false. Body: " + responseBodyString);
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(QuestionPage.this, "Failed to submit score: Empty server response.", Toast.LENGTH_LONG).show();
                            Log.e("SendResult", "HTTP 2xx but empty response body from server.");
                        });
                    }
                } else {
                    final String errorMsg = "Failed to submit score. Server error: " + response.code() + " " + response.message();
                    runOnUiThread(() -> {
                        Toast.makeText(QuestionPage.this, errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("SendResult", errorMsg + (responseBodyString != null ? " Body: " + responseBodyString : ""));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                final String exceptionMsg = "Failed to submit score: " + e.getMessage();
                runOnUiThread(() -> {
                    Toast.makeText(QuestionPage.this, exceptionMsg, Toast.LENGTH_LONG).show();
                    Log.e("SendResult", "Exception during score submission: ", e);
                });
            }
        }).start();

        // Optionally delay finish() if needed to show Toast before exit
        finish();
    }
}
