package com.example.musicmap.screens.settings;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.musicmap.R;
import com.example.musicmap.SessionAndInternetListenerActivity;

public class SettingsActivity extends SessionAndInternetListenerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView backButton = findViewById(R.id.appbarBack);
        backButton.setOnClickListener(view -> super.onBackPressed());
    }

}