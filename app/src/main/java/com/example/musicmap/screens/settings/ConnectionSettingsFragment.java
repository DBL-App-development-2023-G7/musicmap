package com.example.musicmap.screens.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.musicmap.R;
import com.example.musicmap.util.spotify.SpotifyAuthActivity;
import com.example.musicmap.util.ui.Message;

public class ConnectionSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.connection_preferences, rootKey);
        Preference spotifyPreference = findPreference("spotify");

        if (spotifyPreference == null) {
            throw new IllegalStateException("Could not find spotify preference");
        }

        SpotifyAuthActivity activity = (SpotifyAuthActivity) this.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Could not find host activity for ConnectionSettingsFragment");
        }

        spotifyPreference.setOnPreferenceClickListener(preference -> {
            activity.refreshToken(apiToken -> {
                Message.showSuccessMessage(activity, getString(R.string.spotify_already_connected));
            }, activity::registerForSpotifyPKCE);
            return true;
        });
    }

}