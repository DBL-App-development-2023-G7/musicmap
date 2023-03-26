package com.example.musicmap.util.spotify;

import android.util.Log;

import se.michaelthelin.spotify.SpotifyApi;

/**
 * A storage class which stores some global varibles
 * Using static classes is not ideal but it is easy to do
 */
public class SpotifyData {
    private static final String CLIENT_ID = "56ab7fed83514a7a96a7b735737280d8";
    private static final String REDIRECT_URI = "musicmap://spotify-auth";
    private static String token= null;

    private static SpotifyApi spotifyApi;

    public static void setToken(String inputToken){
        token = inputToken;
        Log.d("debug", String.format("[poop] token recieved %s", token));
        spotifyApi = new SpotifyApi.Builder().setAccessToken(token).build();
    }

    public static String getToken() {
        return token;
    }

    public static SpotifyApi getApi() { return  spotifyApi;}
    public static String getClientId(){
        return CLIENT_ID;
    }

    public static String getRedirectUri(){
        return REDIRECT_URI;
    }
}
