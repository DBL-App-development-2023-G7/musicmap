package com.groupseven.musicmap.util.spotify;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.groupseven.musicmap.listeners.SessionListenerActivity;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.util.Constants;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

/**
 * Every activity that wants to do things with Spotify should extend this class.
 * <p>
 * This is because on start of this activity the spotify token is refreshed.
 * The problem is this extends auth activity which is not ideal.
 */
// TODO INSTEAD OF EXTENDING ACTIVITY ADD A LISTENER
public abstract class SpotifyAuthActivity extends SessionListenerActivity {

    private static final String TAG = "SpotifyAuthActivity";

    private static final SpotifyApi loginApi = new SpotifyApi.Builder()
            .setClientId(Constants.SPOTIFY_CLIENT_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(Constants.SPOTIFY_REDIRECT_URI))
            .build();

    private static String codeVerifier = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";

    public void refreshToken(TokenCallback tokenCallback) {
        if (SpotifyData.tokenIsExpired()) {
            String currentUserId = Session.getInstance().getCurrentUser().getUid();
            FirebaseTokenStorage tokenStorage = new FirebaseTokenStorage(currentUserId);

            tokenStorage.getRefreshToken(refreshToken -> {

                if (refreshToken == null) {
                    tokenCallback.onInvalidToken();
                    return;
                }

                loginApi.setRefreshToken(refreshToken);
                loginApi.authorizationCodePKCERefresh().build().executeAsync().handle((refreshResult, error) -> {
                    if (error != null) {
                        Log.d(TAG, String.format("error %s", error.getMessage()));
                        tokenCallback.onInvalidToken();
                        return null;
                    }

                    return refreshResult;
                }).thenAccept(refreshResult -> {
                    Log.d(TAG, "The Spotify token was successfully refreshed!");
                    Log.d(TAG, String.format("ExpiryDate: %d", refreshResult.getExpiresIn()));

                    tokenStorage.storeRefreshToken(refreshResult.getRefreshToken());
                    SpotifyData.setToken(refreshResult.getAccessToken(), refreshResult.getExpiresIn());
                    tokenCallback.onValidToken(refreshResult.getAccessToken());
                });
            });
        }
    }

    public void registerForSpotifyPKCE() {
        codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        AuthorizationCodeUriRequest authorizationCodeUriRequest = loginApi.authorizationCodePKCEUri(codeChallenge)
                .scope("user-read-currently-playing,user-read-recently-played").build();

        authorizationCodeUriRequest.executeAsync().thenAcceptAsync(uri -> {
            Log.d(TAG, String.format("URI: %s", uri.toString()));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
            startActivity(browserIntent);
        });
    }

    private String generateCodeVerifier() {
        final int VERIFIER_LEN = 50;
        byte[] codeVerifier = new byte[VERIFIER_LEN];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(codeVerifier);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    private String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();

            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();

        if (uri != null) {
            String authCode = uri.getQueryParameter("code");
            Log.d(TAG, uri.toString());

            loginApi.authorizationCodePKCE(authCode, codeVerifier).build()
                    .executeAsync()
                    .handle((result, error) -> {
                        if (error != null) {
                            Log.e(TAG, String.format("Error with spotify auth: %s", error.getMessage()));
                        }
                        return result;
                    })
                    .thenAccept(authCredentials -> {
                        Log.d(TAG, "Got Spotify authentication credentials.");

                        String currentUserId = Session.getInstance().getCurrentUser().getUid();
                        FirebaseTokenStorage tokenStorage = new FirebaseTokenStorage(currentUserId);
                        tokenStorage.storeRefreshToken(authCredentials.getRefreshToken());
                        SpotifyData.setToken(authCredentials.getAccessToken(), authCredentials.getExpiresIn());
                    }
            );
        }
    }

    public interface TokenCallback {
        void onValidToken(String apiToken);

        void onInvalidToken();
    }

}
