package com.groupseven.musicmap.screens.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.groupseven.musicmap.BuildConfig;
import com.groupseven.musicmap.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        Preference versionPreference = preferenceScreen.findPreference("version");

        if (versionPreference == null) {
            throw new IllegalStateException("Did not manage to find the version preference.");
        }

        versionPreference.setSummary(getString(R.string.current_version, BuildConfig.VERSION_NAME));
    }

}