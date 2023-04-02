package com.example.musicmap.screens.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.musicmap.R;
import com.example.musicmap.SessionAndInternetListenerActivity;
import com.example.musicmap.screens.profile.ProfileActivity;
import com.example.musicmap.util.ui.FragmentUtil;

public class SettingsActivity extends SessionAndInternetListenerActivity {

    private int currentLayout = R.layout.activity_settings;

    @Override
    protected void updateLayout(boolean internetAvailable) {
        if (!internetAvailable) {
            setContentView(R.layout.no_internet);
            currentLayout = R.layout.no_internet;
            return;
        }

        if (currentLayout == R.layout.activity_settings) {
            return;
        }

        if (currentLayout == R.layout.no_internet) {
            setContentView(R.layout.activity_settings);
            currentLayout = R.layout.activity_settings;
            setupActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupActivity();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentSettings);

        if (currentFragment instanceof AccountSettingsFragment) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
            startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
            finish();
        }
    }

    private void setupActivity() {
        FragmentUtil.initFragment(getSupportFragmentManager(), R.id.fragmentSettings, SettingsFragment.class);
        ImageView backButton = findViewById(R.id.appbarBack);

        backButton.setOnClickListener(view -> {
            this.onBackPressed();
        });
    }

}