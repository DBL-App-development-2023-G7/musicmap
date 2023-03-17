package com.example.musicmap.screens.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.user.Artist;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.FragmentUtil;
import com.example.musicmap.util.ui.Message;
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

        checkInternetPeriodically();

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
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
                    Message.showFailureMessage(this, "Something went wrong");
                }
            }
        });
    }

    /**
     * Starts a periodic check (every 5 seconds) for an internet connection using a Handler.
     * If the connection is lost, shows a message indicating that the connection is lost.
     *
     * @throws SecurityException if the app does not have permission to access the network state
     */
    private void checkInternetPeriodically() throws SecurityException {
        final Handler handler = new Handler();
        final int delay = 5000; // 5 seconds

        handler.postDelayed(new Runnable() {
            public void run() {
                if (!isInternetAvailable()) {
                    Message.showFailureMessage(AuthActivity.this,
                            "Could not detect an internet connection");
                }

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    /**
     * Returns a boolean indicating whether an active network connection is available.
     *
     * @return true if an active network connection is available, false otherwise
     */
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

}