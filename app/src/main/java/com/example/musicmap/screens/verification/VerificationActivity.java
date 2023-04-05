package com.example.musicmap.screens.verification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.musicmap.R;
import com.example.musicmap.SessionAndInternetListenerActivity;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.user.Artist;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;

public class VerificationActivity extends SessionAndInternetListenerActivity {

    private int currentLayout = R.layout.activity_verification;

    @Override
    protected void updateLayout(boolean internetAvailable) {
        if (!internetAvailable) {
            setContentView(R.layout.no_internet);
            currentLayout = R.layout.no_internet;
            return;
        }

        if (currentLayout == R.layout.activity_verification) {
            return;
        }

        if (currentLayout == R.layout.no_internet) {
            setContentView(R.layout.activity_verification);
            currentLayout = R.layout.activity_verification;
            setupActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        setupActivity();
    }

    @Override
    public void onSessionStateChanged() {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) {
            super.onSessionStateChanged();
            return;
        }

        if (!(currentUser instanceof Artist)) {
            throw new IllegalStateException("In VerificationActivity while current user is not an artist");
        }

        Artist currentArtist = (Artist) currentUser;

        if (currentArtist.isVerified()) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finish();
        }
    }

    private void setupActivity() {
        Button signOutVerificationButton = findViewById(R.id.signout_verification_button);
        signOutVerificationButton.setOnClickListener(view -> AuthSystem.logout());
    }

}