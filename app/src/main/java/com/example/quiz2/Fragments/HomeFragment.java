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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quiz2.Activity.MatchPage;
import com.example.quiz2.Adaptors.MatchAdapter;
import com.example.quiz2.DTO.MatchDTO;
import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private TextView userName;
    private EditText opponentUserName;
    private Button randomMatchButton, matchButton;
    private ListView listView;
    private String currentUser;
    private final List<MatchDTO> matchList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userName = view.findViewById(R.id.userName);
        opponentUserName = view.findViewById(R.id.opponentUserName);
        randomMatchButton = view.findViewById(R.id.random_match);
        matchButton = view.findViewById(R.id.match);
        listView = view.findViewById(R.id.listView);

        // Set up item click listener once
        listView.setOnItemClickListener((adapterView, clickedView, position, id) -> {
            if (position >= 0 && position < matchList.size()) {
                MatchDTO selectedMatch = matchList.get(position);
                String opponent = currentUser.equals(selectedMatch.getPlayer1_username())
                        ? selectedMatch.getPlayer2_username()
                        : selectedMatch.getPlayer1_username();
                int matchId = selectedMatch.getMatch_id();

                Intent intent = new Intent(requireContext(), MatchPage.class);
                intent.putExtra("opponentUsername", opponent);
                intent.putExtra("matchId", matchId);
                startActivity(intent);
            }
        });

        APIcall(); // Initial data load

        randomMatchButton.setOnClickListener(v -> randomMatchApiCall());

        matchButton.setOnClickListener(v -> {
            String opponent = opponentUserName.getText().toString().trim();
            if (!opponent.isEmpty()) {
                startMatchWithOpponent(opponent);
            } else {
                Toast.makeText(getContext(), "Enter an opponent username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void randomMatchApiCall() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", "");

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://" + IP.getIp() + "/startRandom")
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    boolean success = Boolean.parseBoolean(responseBody);

                    requireActivity().runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(getContext(), "Random match started!", Toast.LENGTH_SHORT).show();
                            APIcall();
                        } else {
                            Toast.makeText(getContext(), "No available players for random match.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to start random match: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void APIcall() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", "");

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://" + IP.getIp() + "/mainPage")
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseData);
                    currentUser = jsonObject.getString("userName");
                    JSONArray matchesArray = jsonObject.getJSONArray("matches");

                    matchList.clear();
                    for (int i = 0; i < matchesArray.length(); i++) {
                        JSONObject matchObj = matchesArray.getJSONObject(i);
                        int matchId = matchObj.getInt("match_id");
                        String player1 = matchObj.getString("player1_username");
                        String player2 = matchObj.getString("player2_username");
                        matchList.add(new MatchDTO(matchId, player1, player2));
                    }

                    requireActivity().runOnUiThread(() -> {
                        userName.setText(currentUser);
                        MatchAdapter adapter = new MatchAdapter(requireContext(), matchList, currentUser);
                        listView.setAdapter(adapter);
                    });

                } else {
                    Log.e("API_ERROR", "Error: " + response.code() + " - " + response.message());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startMatchWithOpponent(String opponentUsername) {
        new Thread(() -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", "");

            OkHttpClient client = new OkHttpClient();

            String url = "http://" + IP.getIp() + "/start/" + opponentUsername;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    boolean success = Boolean.parseBoolean(responseBody);

                    requireActivity().runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(getContext(), "Match started with " + opponentUsername, Toast.LENGTH_SHORT).show();
                            APIcall();
                        } else {
                            Toast.makeText(getContext(), "Could not start match with " + opponentUsername, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to start match: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
