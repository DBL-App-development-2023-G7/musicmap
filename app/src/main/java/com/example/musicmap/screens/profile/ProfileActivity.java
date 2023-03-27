package com.example.musicmap.screens.profile;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.musicmap.R;
import com.example.musicmap.SessionListenerActivity;

public class ProfileActivity extends SessionListenerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView backButton = findViewById(R.id.appbarBack);
        backButton.setOnClickListener(view -> super.onBackPressed());

        ImageView settingsButton = findViewById(R.id.appbarSettings);
        settingsButton.setOnClickListener(view -> {
            //TODO add go to settings method
        });
    }

}