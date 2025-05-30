package com.example.quiz2.Fragments;

import android.os.Bundle;
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

import com.example.quiz2.Adaptors.MatchAdapter;
import com.example.quiz2.DTO.MatchDTO;
import com.example.quiz2.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private TextView userName;
    private EditText opponentUserName;
    private Button randomMatchButton, matchButton;
    private ListView listView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        String currentUser = "JohnDoe";
        userName.setText("John Doe");
        List<MatchDTO> matchList = new ArrayList<>();
        matchList.add(new MatchDTO(1, "JohnDoe", "Alice"));
        matchList.add(new MatchDTO(2, "Bob", "JohnDoe"));
        matchList.add(new MatchDTO(3, "JohnDoe", "Charlie"));

        ListView listView = view.findViewById(R.id.listView);
        MatchAdapter adapter = new MatchAdapter(requireContext(), matchList, currentUser);
        listView.setAdapter(adapter);

        randomMatchButton.setOnClickListener(v -> {
            // Add logic for random match
        });

        matchButton.setOnClickListener(v -> {
            String opponent = opponentUserName.getText().toString().trim();
            if (!opponent.isEmpty()) {
                // Start match with opponent
            }
        });
        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            MatchDTO selectedMatch = matchList.get(position);

            String opponent = currentUser.equals(selectedMatch.getPlayer1_username())
                    ? selectedMatch.getPlayer2_username()
                    : selectedMatch.getPlayer1_username();

            Toast.makeText(getContext(), "Clicked: " + opponent, Toast.LENGTH_SHORT).show();

            // You can also start a new activity or navigate here.
        });

    }
}
