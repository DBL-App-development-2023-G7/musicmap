package com.example.musicmap.screens.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.musicmap.BuildConfig;
import com.example.musicmap.R;

public class ConnectionSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.connection_preferences, rootKey);
        PreferenceScreen preferenceScreen = getPreferenceScreen();

    }

}