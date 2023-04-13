package com.example.musicmap.screens.verification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.musicmap.R;
import com.example.musicmap.SessionAndInternetListenerActivity;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.models.Artist;
import com.example.musicmap.firebase.Session;
import com.example.musicmap.models.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            setupLayout();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        setupLayout();
    }

    @Override
    public void onSessionStateChanged() {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) {
            super.onSessionStateChanged();
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.reload().addOnCompleteListener(task -> {
                if (firebaseUser.isEmailVerified()) {
                    if (currentUser instanceof Artist && !((Artist) currentUser).isVerified()) {
                        return;
                    }

                    redirectUser();
                }
            });
        }
    }

    private void setupLayout() {
        TextView userMsg = findViewById(R.id.userTextView);
        TextView artistMsg = findViewById(R.id.artistTextView);

        User currentUser = Session.getInstance().getCurrentUser();
        
        if (currentUser instanceof Artist) {
            artistMsg.setVisibility(View.VISIBLE);
            userMsg.setVisibility(View.VISIBLE);
        } else {
            userMsg.setVisibility(View.VISIBLE);
        }

        Button signOutVerificationButton = findViewById(R.id.signout_verification_button);
        signOutVerificationButton.setOnClickListener(view -> AuthSystem.logout());
    }

    private void redirectUser() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

}