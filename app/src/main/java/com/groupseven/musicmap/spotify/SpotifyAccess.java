package com.groupseven.musicmap.spotify;

import android.util.Log;

import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.firebase.SpotifyTokenStorage;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

/**
 * A storage class which stores some global variables.
 */
public final class SpotifyAccess {

    private static volatile SpotifyAccess instance;
    private String token;
    private SpotifyApi spotifyApi;

    private static final SpotifyApi SPOTIFY_LOGIN_API = new SpotifyApi.Builder()
            .setClientId(Constants.SPOTIFY_CLIENT_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(Constants.SPOTIFY_REDIRECT_URI))
            .build();
    private static long tokenExpiryTimeStampMillis;
    private static final String TAG = "SpotifyAccess";

    private SpotifyAccess() {
        this.token = null;
    }

    public static SpotifyAccess getSpotifyAccessInstance() {
        if (instance == null) {
            synchronized (SpotifyAccess.class) {
                if (instance == null) {
                    Log.i(TAG, "Generating a spotify access instance.");
                    instance = new SpotifyAccess();
                }
            }
        }
        return instance;
    }

    public void setToken(String inputToken, long expiryTimeSeconds) {
        token = inputToken;
        spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(token)
                .build();

        Log.d(TAG, String.format("token received %s", token));

        final int EARLIER_REFRESH_TIME_SECONDS = 300;
        long expiryTimeMillis = (expiryTimeSeconds - EARLIER_REFRESH_TIME_SECONDS) * 1000;
        tokenExpiryTimeStampMillis = System.currentTimeMillis() +  expiryTimeMillis;
    }

    public boolean isTokenExpired() {
        return tokenExpiryTimeStampMillis < System.currentTimeMillis();
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    public SpotifyApi getSpotifyLoginApi() {
        return SPOTIFY_LOGIN_API;
    }

    public void refreshToken(SpotifyAccessActivity.TokenCallback tokenCallback) {
        if (isTokenExpired()) {
            String currentUserId = Session.getInstance().getCurrentUser().getUid();
            SpotifyTokenStorage tokenStorage = new SpotifyTokenStorage(currentUserId);

            tokenStorage.getRefreshToken(refreshToken -> {
                if (refreshToken == null) {
                    tokenCallback.onInvalidToken();
                    return;
                }

                SPOTIFY_LOGIN_API.setRefreshToken(refreshToken);
                SPOTIFY_LOGIN_API.authorizationCodePKCERefresh().build().executeAsync().handle((refreshResult, error) -> {
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
                    setToken(refreshResult.getAccessToken(), refreshResult.getExpiresIn());
                    tokenCallback.onValidToken(refreshResult.getAccessToken());
                });
            });
        }
    }

}
