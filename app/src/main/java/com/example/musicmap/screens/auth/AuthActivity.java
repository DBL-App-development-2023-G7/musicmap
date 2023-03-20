package com.example.musicmap.screens.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.util.ui.FragmentUtil;
import com.example.musicmap.util.ui.Message;

public class AuthActivity extends AppCompatActivity {

    private static final int FRAGMENT_CONTAINER_ID = R.id.fragment_container_view;

    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        checkInternetPeriodically();

        if (savedInstanceState == null) {
            FragmentUtil.initFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                    LoginFragment.class);
        }
    }

    /**
     * Loads the login fragment. It replaces the current fragment that is inside the FRAGMENT_CONTAINER.
     */
    public void loadLoginFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                LoginFragment.class);
    }

    /**
     * Loads the register fragment. It replaces the current fragment that is inside the FRAGMENT_CONTAINER.
     */
    public void loadRegisterFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                RegisterFragment.class);
    }

    /**
     * Loads the register artist fragment. It replaces the current fragment that is inside the FRAGMENT_CONTAINER.
     */
    public void loadRegisterArtistFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                RegisterArtistFragment.class);
    }

    public void loadHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        Log.d(TAG, "Started Home Activity");
        finish();
    }

    /**
     * Starts a periodic check (every 5 seconds) for an internet connection using a Handler.
     * If the connection is lost, shows a message indicating that the connection is lost.
     */
    private void checkInternetPeriodically() {
        Handler handler = new Handler();
        int delay = 5000; // 5 seconds

        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    if (!isInternetAvailable()) {
                        Message.showFailureMessage(AuthActivity.this,
                                getString(R.string.error_no_internet));
                    }
                } finally {
                    handler.postDelayed(this, delay);
                }
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

}