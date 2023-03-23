package com.example.musicmap.screens.main;

import android.os.Bundle;

import com.example.musicmap.AuthListenerActivity;
import com.example.musicmap.R;

public class ProfileActivity extends AuthListenerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

}