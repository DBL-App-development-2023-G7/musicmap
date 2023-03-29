package com.example.musicmap.util.spotify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.musicmap.SessionAndInternetListenerActivity;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

/**
 * This every activity that wants to do things with spotify should extend this class
 * This is becvause on start of this activity the spotify token is refreshed
 * The problem is this extends auth acitivity which is not ideal
 */
// TODO FIND A BETTER WAY OF MANAGING SPOTIFY TOKENS
public abstract class SpotifyAuthActivity extends SessionAndInternetListenerActivity {
    public static final int REQUEST_CODE = 80082; // public so a fragment can use it
    public static final int REQUEST_CODE_2 = 69420;
    private static final String codeChallenge = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";

    public void registerForSpotifyPKCE(){
        URI redirectUri = SpotifyHttpManager.makeUri(SpotifyData.getRedirectUri());
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(SpotifyData.getClientId())
                .setRedirectUri(redirectUri)
                .build();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodePKCEUri(codeChallenge).build();
        authorizationCodeUriRequest.executeAsync().thenAcceptAsync(uri -> {
            Log.d("debug", String.format("[poop] URI: %s", uri.toString()));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,  Uri.parse(uri.toString()));
            startActivityForResult(browserIntent, REQUEST_CODE_2);
        });

        spotifyApi.authorizationCodePKCEUri(codeChallenge);
    }
    public void registerForSpotifyNative() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(
                        SpotifyData.getClientId(),
                        AuthorizationResponse.Type.TOKEN,
                        SpotifyData.getRedirectUri()
                );

        builder.setScopes(new String[]{
                "user-library-read",
                "user-read-private",
                "user-top-read",
                "user-read-recently-played",
                "user-read-currently-playing",
                ""}
        );

        AuthorizationRequest mRequest = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, mRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("debug", String.format("[poop] callback!"));

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
        if (requestCode == REQUEST_CODE_2){
            Log.d("debug", String.format("[poop] responce recieved!"));
            final String EXTRA_AUTH_RESPONSE = "EXTRA_AUTH_RESPONSE";
            final String RESPONSE_KEY = "response";
            // A lot of potential for errors here
            AuthorizationResponse response = intent.getBundleExtra(EXTRA_AUTH_RESPONSE).getParcelable(RESPONSE_KEY);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
