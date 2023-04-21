package com.groupseven.musicmap.screens.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.groupseven.musicmap.spotify.SpotifyAccessActivity;
import com.groupseven.musicmap.util.ui.Message;

public class ConnectionSettingsFragment extends PreferenceFragmentCompat {

    ActivityResultLauncher<Intent> spotifyAccessLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Message.showSuccessMessage(requireActivity(), getString(R.string.spotify_connection_success));
                } else {
                    Message.showFailureMessage(requireActivity(), getString(R.string.spotify_connection_failure));
                }
            });

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
                public void onValidToken() {
                    Message.showSuccessMessage(activity, getString(R.string.spotify_already_connected));
                }

                @Override
                public void onInvalidToken() {
                    Intent startSpotifyAccessIntent = new Intent(requireContext(), SpotifyAccessActivity.class);
                    spotifyAccessLauncher.launch(startSpotifyAccessIntent);
                }
            });
            return true;
        });
    }

}