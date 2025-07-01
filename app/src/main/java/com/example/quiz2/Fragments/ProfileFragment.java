package com.example.quiz2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quiz2.Activity.AllCategoryChooser;
import com.example.quiz2.Activity.MatchPage;
import com.example.quiz2.DTO.ProfileDTO;
import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvTotalMatches, tvWinMatches, tvAverage, tvTotalRank, tvMonthlyRank, tvWeeklyRank;
    private Button btnAddQuestion;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvTotalMatches = view.findViewById(R.id.tvTotalMatches);
        tvWinMatches = view.findViewById(R.id.tvWinMatches);
        tvAverage = view.findViewById(R.id.tvAverage);
        tvTotalRank = view.findViewById(R.id.tvTotalRank);
        tvMonthlyRank = view.findViewById(R.id.tvMonthlyRank);
        tvWeeklyRank = view.findViewById(R.id.tvWeeklyRank);
        btnAddQuestion = view.findViewById(R.id.btnAddQuestion);

        btnAddQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AllCategoryChooser.class);
            startActivity(intent);
        });

        fetchProfileData();
    }

    private void fetchProfileData() {
        new Thread(() -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("jwt_token", "");

            OkHttpClient client = new OkHttpClient();
            String url = "http://" + IP.getIp() + "/getProfile";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);

                    // Parse JSON to ProfileDTO
                    ProfileDTO profile = new ProfileDTO();
                    profile.setUserName(jsonObject.optString("userName"));
                    profile.setTotalMatches(jsonObject.optInt("totalMatches"));
                    profile.setWinMatches(jsonObject.optInt("winMatches"));
                    profile.setAvrage(jsonObject.optDouble("avrage"));
                    profile.setTotalRank(jsonObject.optInt("totalRank"));
                    profile.setMonthlyRank(jsonObject.optInt("monthlyRank"));
                    profile.setWeeklyRank(jsonObject.optInt("weeklyRank"));

                    // Update UI on main thread
                    requireActivity().runOnUiThread(() -> bindProfileData(profile));
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Failed to load profile: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void bindProfileData(ProfileDTO dto) {
        tvUsername.setText("Username: " + dto.getUserName());
        tvTotalMatches.setText("Total Matches: " + dto.getTotalMatches());
        tvWinMatches.setText("Win Matches: " + dto.getWinMatches());
        tvAverage.setText("Average Score: " + dto.getAvrage());
        tvTotalRank.setText("Total Rank: " + dto.getTotalRank());
        tvMonthlyRank.setText("Monthly Rank: " + dto.getMonthlyRank());
        tvWeeklyRank.setText("Weekly Rank: " + dto.getWeeklyRank());
    }
}
