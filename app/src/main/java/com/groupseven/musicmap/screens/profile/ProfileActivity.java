package com.groupseven.musicmap.screens.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.listeners.SessionListenerActivity;
import com.groupseven.musicmap.screens.main.HomeActivity;
import com.groupseven.musicmap.screens.settings.SettingsActivity;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.ui.FragmentUtil;

public class ProfileActivity extends SessionListenerActivity {

    private int currentLayout = R.layout.activity_profile;
    private Bundle currentBundle = null;

    @Override
    protected void onInternetStateChange(boolean internetAvailable) {
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
        this.currentBundle = getIntent().getExtras();
        setupActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void setupActivity() {
        FragmentUtil.initFragment(getSupportFragmentManager(), R.id.profileFragment, ProfilePageFragment.class,
                this.currentBundle);

        ImageView backButton = findViewById(R.id.appbarBack);

        backButton.setOnClickListener(view -> {
            this.onBackPressed();
        });

        ImageView settingsButton = findViewById(R.id.appbarSettings);

        // Disable settings button if profile is for a different user
        if (!Session.getInstance().getCurrentUser().getUid().equals(
                getIntent().getStringExtra(Constants.PROFILE_USER_UID_ARGUMENT))) {
            settingsButton.setVisibility(View.INVISIBLE);
        } else {
            settingsButton.setVisibility(View.VISIBLE);
        }

        settingsButton.setOnClickListener(view -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });
    }

}