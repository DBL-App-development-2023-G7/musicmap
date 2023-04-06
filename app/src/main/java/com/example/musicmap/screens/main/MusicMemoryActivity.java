package com.example.musicmap.screens.main;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.musicmap.R;
import com.example.musicmap.SessionAndInternetListenerActivity;
import com.example.musicmap.util.ui.FragmentUtil;

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
