package com.example.musicmap.util.spotify;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.musicmap.SessionAndInternetListenerActivity;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
    private static final String codeVerifyer = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";
    private static final SpotifyApi loginApi = new SpotifyApi.Builder()
            .setClientId(SpotifyData.getClientId())
            .setRedirectUri(SpotifyHttpManager.makeUri(SpotifyData.getRedirectUri()))
            .build();
    public void registerForSpotifyPKCE() {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifyer.getBytes(StandardCharsets.US_ASCII));
            byte[] encoded = Base64.getEncoder().withoutPadding().encode(hash);
            String codeChallenge = new String(encoded);
            Log.d("debug", String.format("[poop] Challenge: %s", codeChallenge));
            AuthorizationCodeUriRequest authorizationCodeUriRequest = loginApi.authorizationCodePKCEUri(codeChallenge)
                    .scope("user-read-currently-playing,user-read-recently-played").build();
            authorizationCodeUriRequest.executeAsync().thenAcceptAsync(uri -> {
                Log.d("debug", String.format("[poop] URI: %s", uri.toString()));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                startActivity(browserIntent);
            });
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null){
            String authCode = uri.getQueryParameter("code");
            Log.d("debug", String.format("[poop] %s", uri.toString()));
            loginApi.authorizationCodePKCE(authCode, codeVerifyer).build()
                    .executeAsync()
                    .thenAccept( authCredentials -> {
                                Log.d("debug", String.format("[poop] pooo!"));
                                Log.d("debug", String.format("[poop] Token: %s", authCredentials.getAccessToken()));
                                Log.d("debug", String.format("[poop] ExpiryDate: %d", authCredentials.getExpiresIn()));
                                Log.d("debug", String.format("[poop] Token type: %s", authCredentials.getTokenType()));
                                Log.d("debug", String.format("[poop] RefreshToken: %s", authCredentials.getRefreshToken()));
                                SpotifyData.setToken(authCredentials.getAccessToken());
                                // TODO updateFirebase
                            }
                    );
        }
    }
}
