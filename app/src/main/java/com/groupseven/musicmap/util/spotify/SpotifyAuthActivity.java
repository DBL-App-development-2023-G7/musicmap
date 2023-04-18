package com.groupseven.musicmap.util.spotify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.groupseven.musicmap.R;
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
public class SpotifyAuthActivity extends AppCompatActivity {

    private static final String TAG = "SpotifyAuthActivity";

    private static final SpotifyApi LOGIN_API = new SpotifyApi.Builder()
            .setClientId(Constants.SPOTIFY_CLIENT_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(Constants.SPOTIFY_REDIRECT_URI))
            .build();

    private static String codeVerifier = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";

    public interface InvalidTokenCallback {
        void onInvalidToken();
    }

    public interface ValidTokenCallback {
        void onValidToken(String apiToken);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_auth);
        registerForSpotifyPKCE();
    }

    public void registerForSpotifyPKCE() {
        codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        AuthorizationCodeUriRequest authorizationCodeUriRequest = LOGIN_API.authorizationCodePKCEUri(codeChallenge)
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
                        FirebaseTokenStorage tokenStorage = new FirebaseTokenStorage(currentUserId);
                        tokenStorage.storeRefreshToken(authCredentials.getRefreshToken());
                        SpotifyData.setToken(authCredentials.getAccessToken(), authCredentials.getExpiresIn());

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
