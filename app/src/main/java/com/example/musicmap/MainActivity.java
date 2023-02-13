package com.example.musicmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText text;
    Button logoutButton;

    private UserInternal retrieveUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        return gson.fromJson(json, UserInternal.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        text = findViewById(R.id.editTextTextPersonName);
        logoutButton = findViewById(R.id.button2);

        UserInternal user = retrieveUserFromSharedPreferences();
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(user.accessToken);

        SpotifyService spotify = api.getService();

        logoutButton.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        });

        spotify.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate user, Response response) {
                Picasso.get().load(user.uri).placeholder(com.google.firebase.appcheck.interop.R.drawable.common_full_open_on_phone).into(imageView);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("ERROR_HERE", "Could not fetch user");
            }
        });

        spotify.getTopTracks(new Callback<Pager<Track>>() {
            @Override
            public void success(Pager<Track> trackPager, Response response) {
                List<Track> tracks = trackPager.items;
                text.setText("Top track: " + tracks.get(1).name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("ERROR_HERE", "Could not fetch top tracks");
            }
        });
    }
}