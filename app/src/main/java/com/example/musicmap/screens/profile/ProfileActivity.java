package com.example.musicmap.screens.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.musicmap.R;
import com.example.musicmap.SessionAndInternetListenerActivity;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.util.ui.FragmentUtil;

public class ProfileActivity extends SessionAndInternetListenerActivity {

    private int currentLayout = R.layout.activity_profile;

    @Override
    protected void updateLayout(boolean internetAvailable) {
        if (!internetAvailable) {
            setContentView(R.layout.no_internet);
            currentLayout = R.layout.no_internet;
            return;
        }

        if (currentLayout == R.layout.activity_profile) {
            return;
        }

        if (currentLayout == R.layout.no_internet) {
            setContentView(R.layout.activity_profile);
            currentLayout = R.layout.activity_profile;
            setupActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupActivity();
    }

    private void setupActivity() {
        FragmentUtil.initFragment(getSupportFragmentManager(), R.id.profileFragment, ProfilePageFragment.class);

        ImageView backButton = findViewById(R.id.appbarBack);
        backButton.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        ImageView settingsButton = findViewById(R.id.appbarSettings);
        settingsButton.setOnClickListener(view -> {
            // TODO: add go to settings method
        });
    }

}