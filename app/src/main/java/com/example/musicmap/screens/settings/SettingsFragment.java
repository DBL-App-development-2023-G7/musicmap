package com.example.musicmap.screens.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.musicmap.BuildConfig;
import com.example.musicmap.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference versionPreference = preferenceScreen.getPreference(preferenceScreen.getPreferenceCount() - 1);
        versionPreference.setSummary("The current version of the app is: " + BuildConfig.VERSION_NAME);
    }
}