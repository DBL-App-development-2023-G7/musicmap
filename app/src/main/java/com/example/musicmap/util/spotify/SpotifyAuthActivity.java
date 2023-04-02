package com.example.musicmap.util.spotify;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.musicmap.SessionAndInternetListenerActivity;
import com.example.musicmap.user.Session;

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
 *
 * This is because on start of this activity the spotify token is refreshed.
 * The problem is this extends auth activity which is not ideal.
 */
// TODO INSTEAD OF EXTENDING ACTIVITY ADD A LISTENER
public abstract class SpotifyAuthActivity extends SessionAndInternetListenerActivity {

    private static final String CLIENT_ID = "56ab7fed83514a7a96a7b735737280d8";
    private static final String REDIRECT_URI = "musicmap://spotify-auth";
    private static final SpotifyApi loginApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(REDIRECT_URI))
            .build();

    private static String codeVerifier = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";

    public interface InvalidTokenCallback {
        void onInvalidToken();
    }

    public interface ValidTokenCallback {
        void onValidToken(String apiToken);
    }

    public void refreshToken(ValidTokenCallback validTokenCallback, InvalidTokenCallback invalidTokenCallback) {

        String currentUserId = Session.getInstance().getCurrentUser().getUid();
        FirebaseTokenStorage tokenStorage = new FirebaseTokenStorage(currentUserId);
        tokenStorage.getRefreshToken(refreshToken -> {
            if (refreshToken == null) {
                invalidTokenCallback.onInvalidToken();
                return;
            }
            loginApi.setRefreshToken(refreshToken);
            loginApi.authorizationCodePKCERefresh().build().executeAsync()
                    .handle((refreshResult, error) -> {
                        if (error != null) {
                            invalidTokenCallback.onInvalidToken();
                            return null;
                        } else return refreshResult;
                    }).thenAccept(refreshResult -> {
                        tokenStorage.storeRefreshToken(refreshResult.getRefreshToken());
                        Log.d("debug", "[poop] Successful refresh!");
                        SpotifyData.setToken(refreshResult.getAccessToken());
                        validTokenCallback.onValidToken(refreshResult.getAccessToken());
                    });
        });
    }

    public void registerForSpotifyPKCE() {
        codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);
        Log.d("debug", String.format("[poop] Verifier: %s", codeVerifier));
        Log.d("debug", String.format("[poop] Challenge: %s", codeChallenge));
        AuthorizationCodeUriRequest authorizationCodeUriRequest = loginApi.authorizationCodePKCEUri(codeChallenge)
                .scope("user-read-currently-playing,user-read-recently-played").build();
        authorizationCodeUriRequest.executeAsync().thenAcceptAsync(uri -> {
            Log.d("debug", String.format("[poop] URI: %s", uri.toString()));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
            startActivity(browserIntent);
        });
    }

    private String generateCodeVerifier() {
        final int VERIFIER_LEN = 50;
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[VERIFIER_LEN];
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
            Log.d("debug", "[poop] This is literally impossible");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("debug", String.format("[poop] Verifier: %s", codeVerifier));
        Uri uri = intent.getData();
        if (uri != null) {
            String authCode = uri.getQueryParameter("code");
            Log.d("debug", String.format("[poop] %s", uri));
            loginApi.authorizationCodePKCE(authCode, codeVerifier).build()
                    .executeAsync()
                    .handle((result, error) -> {
                        if (error != null) {
                            Log.d("debug", String.format("[poop] Error: %s", error.getMessage()));
                        }
                        return result;
                    })
                    .thenAccept(authCredentials -> {
                                Log.d("debug", "[poop] pooo!");
                                Log.d("debug", String.format("[poop] Token: %s", authCredentials.getAccessToken()));
                                Log.d("debug", String.format("[poop] ExpiryDate: %d",
                                        authCredentials.getExpiresIn()));
                                Log.d("debug", String.format("[poop] Token type: %s",
                                        authCredentials.getTokenType()));
                                Log.d("debug", String.format("[poop] RefreshToken: %s",
                                        authCredentials.getRefreshToken()));
                                // TODO updateFirebase
                                String currentUserId = Session.getInstance().getCurrentUser().getUid();
                                FirebaseTokenStorage tokenStorage = new FirebaseTokenStorage(currentUserId);
                                tokenStorage.storeRefreshToken(authCredentials.getRefreshToken());
                            }
                            //CSOFF: Indentation
                    );
            //CSON: Indentation
        }
    }

}
