package com.example.quiz2.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quiz2.DTO.MatchDTO;
import com.example.quiz2.R;

import java.util.List;

public class MatchAdapter extends ArrayAdapter<MatchDTO> {

    private final String currentUsername;

    public MatchAdapter(Context context, List<MatchDTO> matches, String currentUsername) {
        super(context, 0, matches);
        this.currentUsername = currentUsername;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MatchDTO match = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.match_list_item, parent, false);
        }

        TextView opponentText = convertView.findViewById(R.id.opponentUsername);

        if (match != null) {
            // Determine the opponent based on current username
            String opponent = currentUsername.equals(match.getPlayer1_username())
                    ? match.getPlayer2_username()
                    : match.getPlayer1_username();

            opponentText.setText("Opponent: " + opponent);
        }

        return convertView;
    }
}
