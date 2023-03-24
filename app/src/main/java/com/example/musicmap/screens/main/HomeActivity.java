package com.example.musicmap.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.musicmap.R;
import com.example.musicmap.screens.map.MapFragment;
import com.example.musicmap.screens.profile.ProfileActivity;
import com.example.musicmap.user.Session;
import com.example.musicmap.util.ui.FragmentUtil;
import com.example.musicmap.SessionListenerActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends SessionListenerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Session.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ImageView profileButton = findViewById(R.id.appbarProfile);

        profileButton.setOnClickListener(view ->
                startActivity(new Intent(this, ProfileActivity.class)));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // using ifs instead of switch as resource IDs will be non-final by default in
            // Android Gradle Plugin version 8.0, therefore not to be used in switch
            if (item.getItemId() == R.id.navbarFeed) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        FeedFragment.class);
                return true;
            }

            if (item.getItemId() == R.id.navbarPost) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        PostFragment.class);
                return true;
            }

            if (item.getItemId() == R.id.navbarMap) {
                // TODO replace with implementation of MusicMapFragment once made
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        MapFragment.class);
                return true;
            }

            return false;
        });
    }
}