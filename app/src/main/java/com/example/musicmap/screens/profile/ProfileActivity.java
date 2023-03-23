package com.example.musicmap.screens.profile;

import android.os.Bundle;

import com.example.musicmap.R;
import com.example.musicmap.SessionListenerActivity;

public class ProfileActivity extends SessionListenerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

}