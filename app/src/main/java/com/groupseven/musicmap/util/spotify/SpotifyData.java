package com.groupseven.musicmap.util.spotify;

import android.util.Log;

import se.michaelthelin.spotify.SpotifyApi;

/**
 * A storage class which stores some global variables.
 *
 * Using static classes is not ideal but it is easy to do.
 */
public class SpotifyData {

    //TODO this has to be moved in another file
    private static final String CLIENT_ID = "56ab7fed83514a7a96a7b735737280d8";
    private static final String REDIRECT_URI = "musicmap://spotify-auth";

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

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getRedirectUri() {
        return REDIRECT_URI;
    }

}
