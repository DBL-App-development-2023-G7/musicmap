package com.groupseven.musicmap.util.spotify;

import android.util.Log;

import se.michaelthelin.spotify.SpotifyApi;

/**
 * A storage class which stores some global variables.
 */
public class SpotifyData {

    private static String token = null;
    private static SpotifyApi spotifyApi;
    private static long tokenExpiryTimeStampMillis = -1; // the time the token expires in milliseconds

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

}
