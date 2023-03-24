package com.example.musicmap.screens.profile;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.musicmap.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}