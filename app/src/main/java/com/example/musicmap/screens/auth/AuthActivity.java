package com.example.musicmap.screens.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.user.Artist;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.FragmentUtil;

public class AuthActivity extends AppCompatActivity {

    private static final int FRAGMENT_CONTAINER_ID = R.id.fragment_container_view;

    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
    }

    public void loadLoginFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                LoginFragment.class);
    }

    public void loadRegisterFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                RegisterFragment.class);
    }

    public void loadRegisterArtistFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                RegisterArtistFragment.class);
    }

    private void loadVerificationFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                VerificationFragment.class);
    }

    private void loadHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        Log.d(TAG, "Started Home Activity");
        finish();
    }

    public void loadActivityBasedOnVerificationStatus() {
        AuthSystem.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult();
                if (user.isArtist() && !((Artist) user).isVerified()) {
                    loadVerificationFragment();
                } else {
                    loadHomeActivity();
                }
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e(TAG, exception.toString());
                } else {
                    Log.e(TAG, "Unknown error!");
                }
            }
        });
    }

}