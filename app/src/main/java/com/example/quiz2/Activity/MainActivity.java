package com.example.quiz2.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz2.R;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DURATION = 2000;
    private static final String PREF_NAME = "localData";
    int PRIVATE_MODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String token = prefs.getString("jwt_token", null);

                if (token != null) {
                    startActivity(new Intent(MainActivity.this , MainPage.class));
                } else {
                    startActivity(new Intent(MainActivity.this ,  login.class));
                }
            }
        }, SPLASH_SCREEN_DURATION);
    }
}