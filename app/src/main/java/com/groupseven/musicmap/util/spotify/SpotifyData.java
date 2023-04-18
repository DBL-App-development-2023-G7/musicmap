package com.groupseven.musicmap.util.spotify;

import android.util.Log;

import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.util.Constants;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

/**
 * A storage class which stores some global variables.
 */
public class SpotifyData {

    private static String token = null;
    private static SpotifyApi spotifyApi;

    private static final SpotifyApi loginApi = new SpotifyApi.Builder()
            .setClientId(Constants.SPOTIFY_CLIENT_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(Constants.SPOTIFY_REDIRECT_URI))
            .build();
    private static long tokenExpiryTimeStampMillis = -1; // the time the token expires in milliseconds
    private static final String TAG = "SpotifyData";

    public static void setToken(String inputToken, long expiryTimeSeconds) {
        token = inputToken;
        spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(token)
                .build();

        Log.d("debug", String.format("token recieved %s", token));

        final int EARLIER_REFRESH_TIME_SECONDS = 300;
        long expiryTimeMillis = (expiryTimeSeconds - EARLIER_REFRESH_TIME_SECONDS) * 1000;
        tokenExpiryTimeStampMillis = System.currentTimeMillis() +  expiryTimeMillis;
    }

    public static boolean tokenIsExpired() {
        return tokenExpiryTimeStampMillis < System.currentTimeMillis();
    }

    public static String getToken() {
        return token;
    }

    public static SpotifyApi getApi() {
        return spotifyApi;
    }

    public static void refreshToken(SpotifyAuthActivity.ValidTokenCallback validTokenCallback, SpotifyAuthActivity.InvalidTokenCallback invalidTokenCallback) {
        if (SpotifyData.tokenIsExpired()) {
            String currentUserId = Session.getInstance().getCurrentUser().getUid();
            FirebaseTokenStorage tokenStorage = new FirebaseTokenStorage(currentUserId);

            tokenStorage.getRefreshToken(refreshToken -> {
                if (refreshToken == null) {
                    invalidTokenCallback.onInvalidToken();
                    return;
                }

                loginApi.setRefreshToken(refreshToken);
                loginApi.authorizationCodePKCERefresh().build().executeAsync().handle((refreshResult, error) -> {
                    if (error != null) {
                        Log.d(TAG, String.format("error %s", error.getMessage()));
                        invalidTokenCallback.onInvalidToken();
                        return null;
                    }

                    return refreshResult;
                }).thenAccept(refreshResult -> {
                    Log.d(TAG, "The Spotify token was successfully refreshed!");
                    Log.d(TAG, String.format("ExpiryDate: %d", refreshResult.getExpiresIn()));

                    tokenStorage.storeRefreshToken(refreshResult.getRefreshToken());
                    SpotifyData.setToken(refreshResult.getAccessToken(), refreshResult.getExpiresIn());
                    validTokenCallback.onValidToken(refreshResult.getAccessToken());
                });
            });
        }
    }

}
