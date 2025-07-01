package com.example.quiz2.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quiz2.DTO.LoginResponse;
import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword, editTextEmail;
    private Button buttonSignUp;
    private TextView errorText, errorConnectionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // UI components
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        errorText = findViewById(R.id.errorText);
        errorConnectionText = findViewById(R.id.errorConnectionText);

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonSignUp.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String url = "http://" + IP.getIp() + ":8080/api/auth/register";

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(SignUp.this, "Please enter username, email, and password", Toast.LENGTH_SHORT).show();
                return;
            }

            buttonSignUp.setEnabled(false);

            new Thread(() -> {
                MediaType JSON = MediaType.get("application/json; charset=utf-8");
                String json = "{"
                        + "\"userName\":\"" + username + "\","
                        + "\"email\":\"" + email + "\","
                        + "\"password\":\"" + password + "\""
                        + "}";

                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                OkHttpClient client = new OkHttpClient();

                try {
                    Response response = client.newCall(request).execute();
                    String responseBody = response.body().string();
                    Log.d("SIGNUP", "Response code: " + response.code());
                    Log.d("SIGNUP", "Response body: " + responseBody);

                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = new Gson().fromJson(responseBody, LoginResponse.class);
                        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        prefs.edit().putString("jwt_token", loginResponse.getToken()).apply();

                        runOnUiThread(() -> {
                            startActivity(new Intent(SignUp.this, MainPage.class));
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            errorText.setVisibility(View.VISIBLE);
                            new Handler(getMainLooper()).postDelayed(() -> errorText.setVisibility(View.INVISIBLE), 10000);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("SIGNUP", "Exception during signup", e);
                    runOnUiThread(() -> {
                        errorConnectionText.setVisibility(View.VISIBLE);
                        new Handler(getMainLooper()).postDelayed(() -> errorConnectionText.setVisibility(View.INVISIBLE), 10000);
                    });
                } finally {
                    runOnUiThread(() -> buttonSignUp.setEnabled(true));
                }
            }).start();
        });
    }
}
