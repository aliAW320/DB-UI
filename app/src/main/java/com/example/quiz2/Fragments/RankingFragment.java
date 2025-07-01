package com.example.quiz2.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz2.Adaptors.RankingAdapter;
import com.example.quiz2.Adaptors.RankingItem;
import com.example.quiz2.R;
import com.example.quiz2.Tools.IP;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RankingFragment extends Fragment {

    private RadioGroup rgTimeframe;
    private RecyclerView rvRankingList;
    private RankingAdapter adapter;
    private List<RankingItem> rankingItems;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        rgTimeframe = view.findViewById(R.id.rgTimeframe);
        rvRankingList = view.findViewById(R.id.rvRankingList);

        rankingItems = new ArrayList<>();
        adapter = new RankingAdapter(rankingItems);
        rvRankingList.setAdapter(adapter);

        loadData("total");

        rgTimeframe.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTotal) {
                loadData("total");
            } else if (checkedId == R.id.rbMonthly) {
                loadData("monthly");
            } else if (checkedId == R.id.rbWeekly) {
                loadData("weekly");
            }
        });

        return view;
    }

    private void loadData(String timeframe) {
        String endpoint;
        switch (timeframe) {
            case "weekly":
                endpoint = "/weeklyRank";
                break;
            case "monthly":
                endpoint = "/monthlyRanking";
                break;
            case "total":
            default:
                endpoint = "/totalRanking";
                break;
        }

        String url = "http://" + IP.getIp() + endpoint;

        new Thread(() -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            String token = prefs.getString("jwt_token", "");

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try {
                Call call = client.newCall(request);
                Response response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Type listType = new TypeToken<ArrayList<RankingDTO>>() {}.getType();
                    List<RankingDTO> dtoList = gson.fromJson(json, listType);

                    rankingItems.clear();
                    int rank = 1;
                    for (RankingDTO dto : dtoList) {
                        rankingItems.add(new RankingItem(rank++, dto.getName(), dto.getScore()));
                    }

                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                } else {
                    Log.e("RANKING", "Failed to fetch data: " + response.code());
                }
            } catch (IOException e) {
                Log.e("RANKING", "Network error", e);
            }
        }).start();
    }

    static class RankingDTO {
        private String name;
        private int score;

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}
