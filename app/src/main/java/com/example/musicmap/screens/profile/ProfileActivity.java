package com.example.musicmap.screens.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.musicmap.R;
import com.example.musicmap.SessionListenerActivity;
import com.example.musicmap.screens.settings.SettingsActivity;

public class ProfileActivity extends SessionListenerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView backButton = findViewById(R.id.appbarBack);
        backButton.setOnClickListener(view -> super.onBackPressed());

        ImageView settingsButton = findViewById(R.id.appbarSettings);
        settingsButton.setOnClickListener(view -> {
<<<<<<< HEAD
            startActivity(new Intent(this, SettingsActivity.class));
=======
            //TODO add go to settings method
>>>>>>> master
        });
    }

}