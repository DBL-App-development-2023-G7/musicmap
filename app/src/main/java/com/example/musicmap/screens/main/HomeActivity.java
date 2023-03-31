package com.example.musicmap.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.musicmap.R;
import com.example.musicmap.screens.map.PostMapFragment;
import com.example.musicmap.screens.profile.ProfileActivity;
import com.example.musicmap.user.Session;
import com.example.musicmap.util.spotify.SpotifyAuthActivity;
import com.example.musicmap.util.ui.FragmentUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends SpotifyAuthActivity {

    private Class<? extends Fragment> lastFragmentClass = FeedFragment.class;
    private int currentLayout = R.layout.activity_home;

    @Override
    protected void updateLayout(boolean internetAvailable) {
        if (!internetAvailable) {
            setContentView(R.layout.no_internet);
            currentLayout = R.layout.no_internet;
            return;
        }

        if (currentLayout == R.layout.activity_home) {
            return;
        }

        if (currentLayout == R.layout.no_internet) {
            setContentView(R.layout.activity_home);
            currentLayout = R.layout.activity_home;
            setupActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupActivity();
    }

    private void setupActivity() {
        Session.getInstance();

        FragmentUtil.initFragment(getSupportFragmentManager(), R.id.fragment_view, lastFragmentClass);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ImageView profileButton = findViewById(R.id.appbarProfile);

        profileButton.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            finish();
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // using ifs instead of switch as resource IDs will be non-final by default in
            // Android Gradle Plugin version 8.0, therefore not to be used in switch
            if (item.getItemId() == R.id.navbarFeed) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        FeedFragment.class);
                lastFragmentClass = FeedFragment.class;
                return true;
            }

            if (item.getItemId() == R.id.navbarPost) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        PostFragment.class);
                lastFragmentClass = PostFragment.class;
                return true;
            }

            if (item.getItemId() == R.id.navbarMap) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        PostMapFragment.class);
                lastFragmentClass = PostMapFragment.class;
                return true;
            }

            return false;
        });
    }

}