package com.groupseven.musicmap.screens.main.musicmemory.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.listeners.SessionAndInternetListenerActivity;
import com.groupseven.musicmap.screens.main.HomeActivity;
import com.groupseven.musicmap.screens.profile.ProfileActivity;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.ui.FragmentUtil;

public class MusicMemoryActivity extends SessionAndInternetListenerActivity {

    private int currentLayout = R.layout.activity_home;

    @Override
    protected void updateLayout(boolean internetAvailable) {
        if (!internetAvailable) {
            setContentView(R.layout.no_internet);
            currentLayout = R.layout.no_internet;
            return;
        }

        if (currentLayout == R.layout.activity_music_memory) {
            return;
        }

        if (currentLayout == R.layout.no_internet) {
            setContentView(R.layout.activity_music_memory);
            currentLayout = R.layout.activity_music_memory;
            setupActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO: improve back handling with single activity stack
        Bundle bundle = getIntent().getExtras();
        boolean isSentFromFeed = bundle.getBoolean(Constants.IS_SENT_FROM_FEED_ARGUMENT_KEY);

        if (isSentFromFeed) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(Constants.PROFILE_USER_UID_ARGUMENT, bundle.getString(Constants.AUTHOR_UID_ARGUMENT_KEY));
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO better solution then first creating with args and then replacing with new version with args
        setContentView(R.layout.activity_music_memory);

        setupActivity();
    }

    private void setupActivity() {
        FragmentUtil.initFragment(getSupportFragmentManager(), R.id.music_memory_fragment_view,
                MusicMemoryFragment.class, getIntent().getExtras());
    }

}
