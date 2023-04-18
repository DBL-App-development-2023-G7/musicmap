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
public class SpotifyAccessActivity extends AppCompatActivity {

    private static final String TAG = "SpotifyAuthActivity";

    private static final SpotifyApi LOGIN_API = new SpotifyApi.Builder()
            .setClientId(Constants.SPOTIFY_CLIENT_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(Constants.SPOTIFY_REDIRECT_URI))
            .build();

    private static String codeVerifier = Constants.SPOTIFY_DEFAULT_CODE_VERIFIER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_auth);
        registerForSpotifyPKCE();
    }

    public void registerForSpotifyPKCE() {
        codeVerifier = SpotifyUtils.generateCodeVerifier();
        String codeChallenge = SpotifyUtils.generateCodeChallenge(codeVerifier);

        AuthorizationCodeUriRequest authorizationCodeUriRequest = LOGIN_API
                .authorizationCodePKCEUri(codeChallenge)
                .scope(SpotifyUtils.getSpotifyPermissions())
                .build();

        authorizationCodeUriRequest.executeAsync().thenAcceptAsync(uri -> {
            Log.d(TAG, String.format("URI: %s", uri.toString()));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
            startActivity(browserIntent);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();

        if (uri != null) {
            String authCode = uri.getQueryParameter(Constants.SPOTIFY_QUERY_PARAM_KEY);
            Log.d(TAG, uri.toString());

            LOGIN_API.authorizationCodePKCE(authCode, codeVerifier).build()
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
                        SpotifyAccess.setToken(authCredentials.getAccessToken(), authCredentials.getExpiresIn());

                        setResult(Activity.RESULT_OK);
                        finish();
                    }
            );
        }

        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public interface TokenCallback {
        void onValidToken(String apiToken);

        void onInvalidToken();
    }

}
