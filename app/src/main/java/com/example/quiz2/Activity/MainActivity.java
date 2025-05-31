package com.example.quiz2.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz2.R;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE); // <- consistent key
            String token = prefs.getString("jwt_token", null);

            if (token != null && !token.isEmpty()) {
                startActivity(new Intent(MainActivity.this, MainPage.class));
            } else {
                startActivity(new Intent(MainActivity.this, login.class));
            }

            finish(); // <- important to prevent coming back to splash screen
        }, SPLASH_SCREEN_DURATION);
    }
}
