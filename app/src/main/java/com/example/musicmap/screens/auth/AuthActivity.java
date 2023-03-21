package com.example.musicmap.screens.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.user.Artist;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.FragmentUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final int FRAGMENT_CONTAINER_ID = R.id.fragment_container_view;

    private static final String TAG = "AuthActivity";

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);

        firebaseUser = auth.getCurrentUser();

        if (savedInstanceState == null) {
            FragmentUtil.initFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                    LoginFragment.class);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
        firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            loadActivityBasedOnVerificationStatus();
        } else {

            FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                    LoginFragment.class);
        }
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

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

}