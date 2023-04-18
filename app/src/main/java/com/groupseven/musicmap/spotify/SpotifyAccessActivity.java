package com.groupseven.musicmap.spotify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.firebase.SpotifyTokenStorage;
import com.groupseven.musicmap.util.spotify.SpotifyUtils;

import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

/**
 * This class uses {@link SpotifyAccess} to setup Spotify and use the API and PKCE to authenticate.
 */
public class SpotifyAccessActivity extends AppCompatActivity {

    private static final String TAG = "SpotifyAccessActivity";

    /**
     * {@link SpotifyAccess} instance.
     */
    private SpotifyAccess spotifyAccess;

    /**
     * The code verifier used in Spotify authentication flow.
     */
    private String codeVerifier = Constants.SPOTIFY_DEFAULT_CODE_VERIFIER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_auth);
        this.setupSpotify();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setupSpotify();
    }

    /**
     * Initializes the {@link SpotifyAccess} instance, and authenticate with Spotify.
     */
    private void setupSpotify() {
        this.spotifyAccess = SpotifyAccess.getSpotifyAccessInstance();
        registerForSpotifyPKCE();
    }

    /**
     * Registers for PKCE with Spotify and launches a browser intent to authenticate the user.
     *
     * <p>
     * The browser internet asks the user to login only once if the user is a new user who hasn't
     * setup the Spotify connection yet.
     */
    public void registerForSpotifyPKCE() {
        codeVerifier = SpotifyUtils.generateCodeVerifier();
        String codeChallenge = SpotifyUtils.generateCodeChallenge(codeVerifier);

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyAccess.getSpotifyLoginApi()
                .authorizationCodePKCEUri(codeChallenge)
                .scope(SpotifyUtils.getSpotifyPermissions())
                .build();

        authorizationCodeUriRequest.executeAsync().thenAcceptAsync(uri -> {
            Log.d(TAG, String.format("URI: %s", uri.toString()));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
            startActivity(browserIntent);
        });
    }

    /**
     * Handles the Spotify callback and exchanges the authorization code for a refresh token and access token.
     *
     * @param intent The intent containing the authorization code.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();

        if (uri != null) {
            String authCode = uri.getQueryParameter(Constants.SPOTIFY_QUERY_PARAM_KEY);
            Log.d(TAG, uri.toString());

            spotifyAccess.getSpotifyLoginApi().authorizationCodePKCE(authCode, codeVerifier).build()
                    .executeAsync()
                    .handle((result, error) -> {
                        if (error != null) {
                            Log.d(TAG, String.format("Error: %s", error.getMessage()));
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                        }
                        return result;
                    })
                    .thenAccept(authCredentials -> {
                        Log.d(TAG, "Got Spotify authentication credentials.");

                        String currentUserId = Session.getInstance().getCurrentUser().getUid();
                        SpotifyTokenStorage tokenStorage = new SpotifyTokenStorage(currentUserId);
                        tokenStorage.storeRefreshToken(authCredentials.getRefreshToken());
                        spotifyAccess.setToken(authCredentials.getAccessToken(), authCredentials.getExpiresIn());

                        setResult(Activity.RESULT_OK);
                        finish();
                    }
            );
        }

        setResult(Activity.RESULT_CANCELED);
        finish();
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
