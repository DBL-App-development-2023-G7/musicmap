package com.groupseven.musicmap.screens.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.screens.profile.ProfileActivity;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.spotify.SpotifyAuthActivity;
import com.groupseven.musicmap.util.ui.FragmentUtil;

public class SettingsActivity extends SpotifyAuthActivity {

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

        if (currentFragment instanceof AccountSettingsFragment
                || currentFragment instanceof  ConnectionSettingsFragment) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
            User currentUser = Session.getInstance().getCurrentUser();
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(Constants.PROFILE_USER_UID_ARGUMENT, currentUser.getUid());
            startActivity(intent);
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