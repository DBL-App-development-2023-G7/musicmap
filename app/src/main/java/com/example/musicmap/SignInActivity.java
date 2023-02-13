package com.example.musicmap;

import androidx.appcompat.app.AppCompatActivity;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.RetrofitError;
import retrofit.Callback;
import retrofit.client.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SignInActivity extends AppCompatActivity {

    Button signInWithSpotify;

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "myapp://callback";
    private static final String CLIENT_ID = "<client-id-redacted>";

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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    String accessToken = response.getAccessToken();
                    SpotifyApi api = new SpotifyApi();
                    api.setAccessToken(accessToken);

                    SpotifyService spotify = api.getService();

                    spotify.getMe(new Callback<UserPrivate>() {
                        @Override
                        public void success(UserPrivate user, Response response) {
                            DatabaseReference ref = database.getReference("users").child(user.id);
                            ref.child("email").setValue(user.email);
                            ref.child("name").setValue(user.display_name);
                            ref.child("token").setValue(accessToken);

                            UserInternal userInternal = new UserInternal(accessToken, user.display_name, user.id);
                            saveUserToSharedPreferences(userInternal);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("ERROR_HERE", "Could not fetch user");
                        }
                    });

                    break;

                case ERROR:
                    Log.e("ERROR_HERE", "Sign in failed");
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signInWithSpotify = findViewById(R.id.button);

        UserInternal user = retrieveUserFromSharedPreferences();

        if (user != null && !user.id.isEmpty()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }

        signInWithSpotify.setOnClickListener(view -> {
            signInWithSpotify();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
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