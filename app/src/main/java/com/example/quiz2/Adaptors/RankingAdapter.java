package com.example.quiz2.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.quiz2.R;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private final List<RankingItem> rankingList;

    public RankingAdapter(List<RankingItem> rankingList) {
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        RankingItem item = rankingList.get(position);
        holder.tvRankItem.setText(String.valueOf(item.getRank()));
        holder.tvUsernameItem.setText(item.getUsername());
        holder.tvScoreItem.setText(item.getScore() + " pts");
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankItem, tvUsernameItem, tvScoreItem;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRankItem = itemView.findViewById(R.id.tvRankItem);
            tvUsernameItem = itemView.findViewById(R.id.tvUsernameItem);
            tvScoreItem = itemView.findViewById(R.id.tvScoreItem);
        }
    }
}
