package com.groupseven.musicmap.screens.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.listeners.SessionListenerActivity;
import com.groupseven.musicmap.spotify.SpotifyAccessActivity;
import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.groupseven.musicmap.util.ui.Message;

public class ConnectionSettingsFragment extends PreferenceFragmentCompat {

    private final ActivityResultLauncher<Intent> spotifyAuthActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent resultIntent = result.getData();
                Log.d(this.getClass().getName(), "Poop!");
            }
    );

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.connection_preferences, rootKey);
        Preference spotifyPreference = findPreference("spotify");

        if (spotifyPreference == null) {
            throw new IllegalStateException("Could not find spotify preference");
        }

        SessionListenerActivity activity = (SessionListenerActivity) this.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Could not find host activity for ConnectionSettingsFragment");
        }

        spotifyPreference.setOnPreferenceClickListener(preference -> {
            SpotifyAccess.refreshToken(new SpotifyAccessActivity.TokenCallback() {
                @Override
                public void onValidToken(String apiToken) {
                    Message.showSuccessMessage(activity, getString(R.string.spotify_already_connected));
                }

                @Override
                public void onInvalidToken() {
                    Intent spotifyAuthIntent = new Intent(ConnectionSettingsFragment.this.requireActivity(), SpotifyAccessActivity.class);
                    spotifyAuthActivityResultLauncher.launch(spotifyAuthIntent);
                }
            });
            return true;
        });
    }

}