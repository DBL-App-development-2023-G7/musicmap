package com.example.musicmap.screens.verification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.musicmap.R;
import com.example.musicmap.SessionListenerActivity;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.user.Artist;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;

public class VerificationActivity extends SessionListenerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Button signOutVerificationButton = findViewById(R.id.signout_verification_button);
        signOutVerificationButton.setOnClickListener(view -> AuthSystem.logout());
    }

    @Override
    public void onSessionStateChanged() {
        User currentUser = Session.getInstance().getCurrentUser();
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

}