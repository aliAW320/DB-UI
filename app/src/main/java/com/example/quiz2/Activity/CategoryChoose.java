package com.example.quiz2.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz2.DTO.Category;
import com.example.quiz2.DTO.CategoryEntryDTO;
import com.example.quiz2.DTO.QuestionDTO;
import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.util.Log;


public class CategoryChoose extends AppCompatActivity {

    private Button category1, category2, category3;
    private int matchId;
    private ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_choose);

        category1 = findViewById(R.id.category1Button);
        category2 = findViewById(R.id.category2Button);
        category3 = findViewById(R.id.category3Button);

        matchId = getIntent().getIntExtra("matchId", -1);
        categories = (ArrayList<Category>) getIntent().getSerializableExtra("categories");

        if (categories == null || categories.size() < 3) {
            Toast.makeText(this, "No categories received!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set button texts
        category1.setText(categories.get(0).getName());
        category2.setText(categories.get(1).getName());
        category3.setText(categories.get(2).getName());

        // Set click listeners using correct categoryId
        category1.setOnClickListener(v -> sendCategory(categories.get(0).getCategory_id()));
        category2.setOnClickListener(v -> sendCategory(categories.get(1).getCategory_id()));
        category3.setOnClickListener(v -> sendCategory(categories.get(2).getCategory_id()));
    }

    private void sendCategory(int categoryId) {
        new Thread(() -> {
            try {
                CategoryEntryDTO dto = new CategoryEntryDTO(categoryId, matchId);
                Gson gson = new Gson();
                String json = gson.toJson(dto);

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("jwt_token", "");

                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                String url = "http://" + IP.getIp() + "/category/getQuestions";

                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String resStr = response.body().string();

                        Type listType = new TypeToken<ArrayList<QuestionDTO>>() {}.getType();
                        ArrayList<QuestionDTO> questions = gson.fromJson(resStr, listType);

                        runOnUiThread(() -> {
                            Intent intent = new Intent(CategoryChoose.this, QuestionPage.class);
                            intent.putExtra("questions", questions);
                            intent.putExtra("matchId", matchId);
                            startActivity(intent);
                            finish();
                        });

                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
