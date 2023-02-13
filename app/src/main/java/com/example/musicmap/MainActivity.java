package com.example.musicmap;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "UNIQUE_TAG";
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "a945c2c2c85a4ed0883953c4542da317";
    private static final String REDIRECT_URI = "myapp://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    EditText text1;
    EditText text2;
    ImageView image;

    private void saveUserToSharedPreferences(UserInternal user) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.apply();
    }

    private UserInternal retrieveUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        return gson.fromJson(json, UserInternal.class);
    }

    private void signInWithSpotify() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-library-read", "playlist-read-private", "user-read-private", "user-top-read"});
        AuthorizationRequest mRequest = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, mRequest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = findViewById(R.id.editTextTextPersonName);
        text2 = findViewById(R.id.editTextTextPersonName2);
        image = findViewById(R.id.imageView);
        signInWithSpotify();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    String accessToken = response.getAccessToken();
                    Log.d(TAG, accessToken);

                    ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI)
                            .showAuthView(true)
                            .build();

                    SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            Log.d(TAG, "successful connection");
                            connected();
                        }

                        public void onFailure(Throwable throwable) {
                            Log.e(TAG, throwable.getMessage(), throwable);
                        }
                    });
                    break;

                case ERROR:
                    Log.e(TAG, "Sign in failed " + resultCode + " " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            text1.setText(playerState.track.name);
            text2.setText(playerState.isPaused ? "paused" : "playing");
            mSpotifyAppRemote.getImagesApi().getImage(playerState.track.imageUri).setResultCallback(bitmap -> {
                image.setImageBitmap(bitmap);
            });
        });
    }
}

class UserInternal {
    String accessToken;
    String name;
    String id;

    public UserInternal(String accessToken, String name, String id) {
        this.accessToken = accessToken;
        this.name = name;
        this.id = id;
    }
}