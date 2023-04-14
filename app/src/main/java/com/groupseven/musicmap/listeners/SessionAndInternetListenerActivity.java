package com.groupseven.musicmap.listeners;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.models.Artist;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.screens.auth.AuthActivity;
import com.groupseven.musicmap.screens.verification.VerificationActivity;

public abstract class SessionAndInternetListenerActivity extends InternetListenerActivity implements Session.Listener {

    private Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = Session.getInstance();
        session.addListener(this);
    }

    @Override
    @CallSuper
    public void onSessionStateChanged() {
        if (!session.isUserConnected()) {
            loadAuthActivity();
        }
        if (session.isUserLoaded()) {
            User currentUser = session.getCurrentUser();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser.isArtist() && !((Artist) currentUser).isVerified()) {
                loadVerificationActivity();
            } else if (firebaseUser != null) {
                firebaseUser.reload().addOnCompleteListener(task -> {
                    if (!firebaseUser.isEmailVerified()) {
                        loadVerificationActivity();
                    }
                });
            }
        }
    }

    private void loadVerificationActivity() {
        Intent verificationIntent = new Intent(this, VerificationActivity.class);
        verificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(verificationIntent);
        finish();
    }

    private void loadAuthActivity() {
        Intent authIntent = new Intent(this, AuthActivity.class);
        authIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(authIntent);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        session.removeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.addListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        session.removeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.removeListener(this);
    }

}
