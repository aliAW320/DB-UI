package com.example.quiz2.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.quiz2.Fragments.HomeFragment;
import com.example.quiz2.Fragments.ProfileFragment;
import com.example.quiz2.Fragments.RankingFragment;
import com.example.quiz2.R;
import com.google.android.material.tabs.TabLayout;

public class MainPage extends AppCompatActivity {

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        // Your XML above

        tabLayout = findViewById(R.id.tabLayout);

        // Default fragment (Home)
        loadFragment(new HomeFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;

                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new ProfileFragment();
                        break;
                    case 1:
                        selectedFragment = new HomeFragment();
                        break;
                    case 2:
                        selectedFragment = new RankingFragment();
                        break;
                }

                loadFragment(selectedFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)  // Add a FrameLayout for this
                .commit();
    }
}
