package com.example.musicmap.screens.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.util.ui.FragmentUtil;

public class AuthActivity extends AppCompatActivity {

    private static final int FRAGMENT_CONTAINER_ID = R.id.fragment_container_view;

    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

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

}