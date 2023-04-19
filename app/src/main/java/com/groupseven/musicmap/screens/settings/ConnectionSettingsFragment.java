package com.groupseven.musicmap.screens.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.groupseven.musicmap.util.ui.Message;

public class ConnectionSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.connection_preferences, rootKey);
        Preference spotifyPreference = findPreference("spotify");

        if (spotifyPreference == null) {
            throw new IllegalStateException("Could not find spotify preference");
        }

        SettingsActivity activity = (SettingsActivity) this.requireActivity();
        SpotifyAccess spotifyAccess = SpotifyAccess.getSpotifyAccessInstance();

        spotifyPreference.setOnPreferenceClickListener(preference -> {
            spotifyAccess.refreshToken(new SpotifyAccess.TokenCallback() {
                @Override
                public void onValidToken(String apiToken) {
                    Message.showSuccessMessage(activity, getString(R.string.spotify_already_connected));
                }

                @Override
                public void onInvalidToken() {
                    activity.registerForSpotifyPKCE();
                }
            });
            return true;
        });
    }

}