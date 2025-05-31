package com.example.quiz2.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz2.DTO.Category;
import com.example.quiz2.DTO.QuestionDTO;
import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MatchPage extends AppCompatActivity {

    private TextView opponentUserName;
    private TextView userScore;
    private TextView opponentScore;
    private Button playButton;
    private Button backButton;

    private int matchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_page);

        opponentUserName = findViewById(R.id.opponentUserName);
        userScore = findViewById(R.id.UserScore);
        opponentScore = findViewById(R.id.opponentScore);
        playButton = findViewById(R.id.playButton);
        backButton = findViewById(R.id.backButton);

        // Get the match ID passed from previous activity
        matchId = getIntent().getIntExtra("matchId", -1);
        if (matchId == -1) {
            Toast.makeText(this, "No match ID passed!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load match data on start
        loadMatchData(matchId);

        playButton.setOnClickListener(v -> playRoundApi(matchId));

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MatchPage.this, MainPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadMatchData(int matchId) {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", "");

            OkHttpClient client = new OkHttpClient();
            String url = "http://" + IP.getIp() + "/give/" + matchId;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String resStr = response.body().string();
                    JSONObject jsonObject = new JSONObject(resStr);

                    final String opponent = jsonObject.getString("opponent");
                    final int user_score = jsonObject.getInt("user_score");
                    final int opponent_score = jsonObject.getInt("opponent_score");

                    runOnUiThread(() -> {
                        opponentUserName.setText(opponent);
                        userScore.setText(String.valueOf(user_score));
                        opponentScore.setText(String.valueOf(opponent_score));
                    });

                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MatchPage.this, "Failed to load match data: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MatchPage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void playRoundApi(int matchId) {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", "");

            OkHttpClient client = new OkHttpClient();

            String url = "http://" + IP.getIp() + "/round/playRound";

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(String.valueOf(matchId), JSON);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseString);

                    String message = jsonResponse.getString("massage");  // backend typo
                    JSONArray objectsArray = jsonResponse.getJSONArray("objects");

                    runOnUiThread(() -> {
                        try {
                            if ("Category".equals(message)) {
                                List<Category> categories = new ArrayList<>();
                                for (int i = 0; i < objectsArray.length(); i++) {
                                    JSONObject catObj = objectsArray.getJSONObject(i);
                                    int id = catObj.getInt("category_id");
                                    String name = catObj.getString("name");
                                    categories.add(new Category(id, name));
                                }
                                Toast.makeText(this, "Categories loaded: " + categories.size(), Toast.LENGTH_SHORT).show();

                            } else if ("Question".equals(message)) {
                                List<QuestionDTO> questions = new ArrayList<>();
                                for (int i = 0; i < objectsArray.length(); i++) {
                                    JSONObject qObj = objectsArray.getJSONObject(i);
                                    String question = qObj.getString("question");
                                    String option1 = qObj.getString("option1");
                                    String option2 = qObj.getString("option2");
                                    String option3 = qObj.getString("option3");
                                    String option4 = qObj.getString("option4");
                                    int answer = qObj.getInt("answer");

                                    questions.add(new QuestionDTO(question, option1, option2, option3, option4, answer));
                                }
                                Toast.makeText(this, "Questions loaded: " + questions.size(), Toast.LENGTH_SHORT).show();
                                // TODO: Show questions in UI
                            } else {
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
