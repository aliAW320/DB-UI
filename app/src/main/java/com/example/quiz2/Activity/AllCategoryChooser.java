package com.example.quiz2.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllCategoryChooser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private com.example.quiz2.Activity.CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_category_chooser);

        recyclerView = findViewById(R.id.rvCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new com.example.quiz2.Activity.CategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchCategories();
    }

    private void fetchCategories() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", "");
        String url = "http://" + IP.getIp() + "/getAllCategory";

        new Thread(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Type listType = new TypeToken<ArrayList<Category>>() {}.getType();
                    List<Category> categories = gson.fromJson(json, listType);

                    if (categories != null) {
                        runOnUiThread(() -> {
                            categoryList.clear();
                            categoryList.addAll(categories);
                            adapter.notifyDataSetChanged();
                        });
                    }
                } else {
                    showError("Server error: " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
                showError("Network error");
            }
        }).start();
    }

    private void showError(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    public static class Category {
        private int category_id;
        private String name;

        public int getCategory_id() {
            return category_id;
        }

        public String getName() {
            return name;
        }
    }
}
