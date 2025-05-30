package com.example.quiz2.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.quiz2.Tools.IP;
import com.example.quiz2.DTO.LoginResponse;

import com.example.quiz2.R;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends AppCompatActivity {

    private EditText editTextuserName;

    private EditText editTextPassword;
    private Button buttonLogin;
    private Button signUpButton;
    private TextView errorText;
    private TextView errorConnectionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        editTextuserName = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        signUpButton = findViewById(R.id.buttonSignUp);
        errorText = findViewById(R.id.errorText);
        errorConnectionText = findViewById(R.id.errorConnectionText);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username= editTextuserName.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String url = "http://" + IP.getIp() + "/api/auth/login";

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MediaType JSON = MediaType.get("application/json; charset=utf-8");
                            String json = "{\"userName\":\"" + username + "\", \"password\":\"" + password + "\"}";

                            RequestBody requestBody = RequestBody.create(json, JSON);
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();
                            OkHttpClient client = new OkHttpClient();
                            Call call = client.newCall(request);

                            try {
                                Response response = call.execute();

                                if (response.code() == 200) {
                                    LoginResponse loginResponse = new Gson().fromJson(response.body().string(), LoginResponse.class);
                                    // After successful login
                                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("jwt_token", loginResponse.getToken());
                                    editor.apply();


                                    runOnUiThread(() -> {
                                        //Toast.makeText(login.this, "Login successful: " + loginResponse.getToken(), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(login.this , MainPage.class));
                                    });

                                } else {
                                    runOnUiThread(() -> errorText.setVisibility(View.VISIBLE));
                                    try {
                                        Thread.sleep(10000);
                                        runOnUiThread(() -> errorConnectionText.setVisibility(View.INVISIBLE));

                                    } catch (InterruptedException ex) {
                                        ex.toString();
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(() -> errorConnectionText.setVisibility(View.VISIBLE));
                                try {
                                    Thread.sleep(10000);
                                    runOnUiThread(() -> errorConnectionText.setVisibility(View.INVISIBLE));

                                } catch (InterruptedException ex) {
                                    ex.toString();
                                }

                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        });
    }
}