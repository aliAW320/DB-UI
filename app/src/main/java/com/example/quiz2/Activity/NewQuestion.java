package com.example.quiz2.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

public class NewQuestion extends AppCompatActivity {

    EditText questionEdit, option1Edit, option2Edit, option3Edit, option4Edit;
    EditText answerEdit, levelEdit;
    Button submitBtn;
    int categoryId;
    String token; // JWT token

    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        questionEdit = findViewById(R.id.editQuestion);
        option1Edit = findViewById(R.id.editOption1);
        option2Edit = findViewById(R.id.editOption2);
        option3Edit = findViewById(R.id.editOption3);
        option4Edit = findViewById(R.id.editOption4);
        answerEdit = findViewById(R.id.editAnswer);
        levelEdit = findViewById(R.id.editLevel);
        submitBtn = findViewById(R.id.buttonSubmit);

        categoryId = getIntent().getIntExtra("category_id", -1);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", ""); // FIXED: assign to field, not local var

        // Setup OkHttp client with logging interceptor for debugging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d("OkHttp", message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        submitBtn.setOnClickListener(view -> submitQuestion());
    }

    private void submitQuestion() {
        String question = questionEdit.getText().toString().trim();
        String option1 = option1Edit.getText().toString().trim();
        String option2 = option2Edit.getText().toString().trim();
        String option3 = option3Edit.getText().toString().trim();
        String option4 = option4Edit.getText().toString().trim();
        String answerStr = answerEdit.getText().toString().trim();
        String levelStr = levelEdit.getText().toString().trim();

        if (question.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty() || option4.isEmpty()
                || answerStr.isEmpty() || levelStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int answer;
        int level;
        try {
            answer = Integer.parseInt(answerStr);
            level = Integer.parseInt(levelStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Answer and Level must be numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (answer < 1 || answer > 4) {
            Toast.makeText(this, "Correct answer must be between 1 and 4", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("question", question);
            json.put("option1", option1);
            json.put("option2", option2);
            json.put("option3", option3);
            json.put("option4", option4);
            json.put("category_id", categoryId);
            json.put("answer", answer);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://" + IP.getIp() + "/addQuestion";
        Log.d("NewQuestion", "Token: " + token);
        Log.d("NewQuestion", "URL: " + url);
        Log.d("NewQuestion", "Payload: " + json.toString());

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(NewQuestion.this, "Connection failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                Log.e("NewQuestion", "Request failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body() != null ? response.body().string() : "";
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(NewQuestion.this, "Question submitted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NewQuestion.this, "Failed to submit question: " + responseBody, Toast.LENGTH_LONG).show();
                        Log.e("NewQuestion", "Response error: " + responseBody);
                    }
                });
            }
        });
    }
}
