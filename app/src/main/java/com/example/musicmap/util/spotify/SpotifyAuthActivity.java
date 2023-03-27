package com.example.musicmap.util.spotify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.musicmap.SessionListenerActivity;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

/**
 * This every activity that wants to do things with spotify should extend this class
 * This is becvause on start of this activity the spotify token is refreshed
 * The problem is this extends auth acitivity which is not ideal
 */
// TODO FIND A BETTER WAY OF MANAGING SPOTIFY TOKENS
public abstract class SpotifyAuthActivity extends SessionListenerActivity {
    private static final int REQUEST_CODE = 80082;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForSpotify();
    }

    private void registerForSpotify() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(
                        SpotifyData.getClientId(),
                        AuthorizationResponse.Type.TOKEN,
                        SpotifyData.getRedirectUri()
                );

        builder.setScopes(new String[]{
                "user-library-read",
                "playlist-read-private",
                "user-read-private",
                "user-top-read",
                "user-read-recently-played",
                ""}
        );

        AuthorizationRequest mRequest = builder.build();
        startActivityForResult(AuthorizationClient.createLoginActivityIntent(this, mRequest), REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    String accessToken = response.getAccessToken();
                    Log.d("debug", String.format("[poop] Token: %s", accessToken));
                    SpotifyData.setToken(accessToken);
                    break;

                case ERROR:
                    Log.d("debug", String.format("[poop] Sign in failed %s", response.getError()));
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d("debug", String.format("[poop] default"));
                    // Handle other cases
            }
        }
    }
}
