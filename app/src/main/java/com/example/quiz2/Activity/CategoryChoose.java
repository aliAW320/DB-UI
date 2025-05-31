package com.example.quiz2.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz2.R;

public class CategoryChoose extends AppCompatActivity {

    private Button category1, category2, category3;
    private int matchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_choose);

        category1 = findViewById(R.id.category1Button);
        category2 = findViewById(R.id.category2Button);
        category3 = findViewById(R.id.category3Button);

        matchId = getIntent().getIntExtra("matchId", -1);

        category1.setOnClickListener(v -> chooseCategory(category1.getText().toString()));
        category2.setOnClickListener(v -> chooseCategory(category2.getText().toString()));
        category3.setOnClickListener(v -> chooseCategory(category3.getText().toString()));
    }

    private void chooseCategory(String category) {
        // TODO: Send the chosen category to your backend and move to next activity (like a quiz screen)
        Toast.makeText(this, "Chosen: " + category + " for Match ID: " + matchId, Toast.LENGTH_SHORT).show();

        // Example: Intent to move to quiz screen
        // Intent intent = new Intent(this, QuizActivity.class);
        // intent.putExtra("category", category);
        // startActivity(intent);
    }
}
