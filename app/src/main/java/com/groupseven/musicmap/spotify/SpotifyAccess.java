package com.groupseven.musicmap.spotify;

import android.util.Log;

import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.firebase.SpotifyTokenStorage;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

/**
 * This class provides access to the Spotify API is the SOT for the Spotify {@link SpotifyAccess#token},
 * {@link SpotifyAccess#SPOTIFY_LOGIN_API}, and {@link SpotifyAccess#spotifyDataApi}.
 */
public final class SpotifyAccess {

    private static final String TAG = "SpotifyAccess";

    /**
     * The {@link SpotifyAccess} instance.
     */
    private static volatile SpotifyAccess instance;

    /**
     * The Spotify token used to interact with Spotify API to request data.
     */
    private String token;

    /**
     * The Spotify API for fetching data.
     */
    private SpotifyApi spotifyDataApi;

    /**
     * The Spotify API for login with Spotify authentication.
     */
    private static final SpotifyApi SPOTIFY_LOGIN_API = new SpotifyApi.Builder()
            .setClientId(Constants.SPOTIFY_CLIENT_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(Constants.SPOTIFY_REDIRECT_URI))
            .build();

    /**
     * Time in milliseconds for the token to expire.
     */
    private static long tokenExpiryTimeStampMillis;

    /**
     * Private constructor for Singleton class.
     */
    private SpotifyAccess() {
        this.token = null;
    }

    /**
     * Returns an instance of SpotifyAccess.
     *
     * @return The instance of SpotifyAccess.
     */
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

    /**
     * Sets the token and expiration timestamp for the SpotifyApi instance.
     *
     * @param inputToken The token string to be set.
     * @param expiryTimeSeconds The expiry time of the token in seconds.
     */
    public void setToken(String inputToken, long expiryTimeSeconds) {
        Log.d(TAG, String.format("token received %s", token));

        token = inputToken;
        spotifyDataApi = new SpotifyApi.Builder().setAccessToken(token).build();

        final int EARLIER_REFRESH_TIME_SECONDS = 300;
        long expiryTimeMillis = (expiryTimeSeconds - EARLIER_REFRESH_TIME_SECONDS) * 1000;
        tokenExpiryTimeStampMillis = System.currentTimeMillis() +  expiryTimeMillis;
    }

    /**
     * Checks if the token is expired.
     *
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    public boolean isTokenExpired() {
        return tokenExpiryTimeStampMillis < System.currentTimeMillis();
    }

    /**
     * Returns the SpotifyApi instance for fetching data.
     *
     * @return The SpotifyApi instance.
     */
    public SpotifyApi getSpotifyDataApi() {
        return spotifyDataApi;
    }

    /**
     * Returns the SpotifyApi instance for logging in with Spotify Authentication.
     *
     * @return The SpotifyApi instance.
     */
    public SpotifyApi getSpotifyLoginApi() {
        return SPOTIFY_LOGIN_API;
    }

    /**
     * Refreshes the Spotify token if it is expired.
     *
     * @param tokenCallback The token callback to be called when the token is refreshed.
     */
    public void refreshToken(TokenCallback tokenCallback) {
        if (isTokenExpired()) {
            String currentUserId = Session.getInstance().getCurrentUser().getUid();
            SpotifyTokenStorage tokenStorage = new SpotifyTokenStorage(currentUserId);

            tokenStorage.getRefreshToken(refreshToken -> {
                if (refreshToken == null) {
                    tokenCallback.onInvalidToken();
                    return;
                }

                SPOTIFY_LOGIN_API.setRefreshToken(refreshToken);
                SPOTIFY_LOGIN_API.authorizationCodePKCERefresh()
                        .build()
                        .executeAsync()
                        .handle((refreshResult, error) -> {
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

    /**
     * Interface used to be notified of the status of the Spotify access token.
     */
    public interface TokenCallback {

        /**
         * Called when the Spotify access token is valid.
         *
         * @param apiToken The Spotify access token.
         */
        void onValidToken(String apiToken);

        /**
         * Called when the Spotify access token is invalid or has expired.
         */
        void onInvalidToken();
    }

}
