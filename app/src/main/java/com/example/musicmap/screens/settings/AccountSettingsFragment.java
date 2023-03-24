package com.example.musicmap.screens.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.musicmap.R;

public class AccountSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey);
    }
}