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

/**
 * A base activity class that handles listening to changes in the user's session state, and internet
 * connectivity through {@link InternetListenerActivity}.
 */
public abstract class SessionListenerActivity extends InternetListenerActivity implements Session.Listener {

    private Session session;

    /**
     * Initializes the {@link Session} instance and adds the {@link SessionListenerActivity}
     * instance as a listener to the session.
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = Session.getInstance();
        session.addListener(this);
    }

    /**
     * Called when the session state changes.
     * If the user is not connected, the {@link #loadAuthActivity()} method is called.
     * If the user is an artist and is not verified, the {@link #loadVerificationActivity()} method is called.
     * If the user is a Firebase user and their email is not verified, the {@link #loadVerificationActivity()}
     * method is called.
     */
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

    /**
     * Loads the {@link VerificationActivity}.
     * Sets the flags {@link Intent#FLAG_ACTIVITY_NEW_TASK} and {@link Intent#FLAG_ACTIVITY_CLEAR_TASK}
     * to clear the activity stack and start the verification activity as a new task.
     */
    private void loadVerificationActivity() {
        Intent verificationIntent = new Intent(this, VerificationActivity.class);
        verificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(verificationIntent);
        finish();
    }

    /**
     * Loads the {@link AuthActivity}.
     * Sets the flags {@link Intent#FLAG_ACTIVITY_NEW_TASK} and {@link Intent#FLAG_ACTIVITY_CLEAR_TASK}
     * to clear the activity stack and start the auth activity as a new task.
     */
    private void loadAuthActivity() {
        Intent authIntent = new Intent(this, AuthActivity.class);
        authIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(authIntent);
        finish();
    }

    /**
     * Removes the {@link SessionListenerActivity} instance as a listener from the session when the activity
     * is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        session.removeListener(this);
    }

    /**
     * Adds the {@link SessionListenerActivity} instance as a listener to the session when the activity
     * is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        session.addListener(this);
    }

    /**
     * Removes the {@link SessionListenerActivity} instance as a listener from the session when the activity
     * is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        session.removeListener(this);
    }

    /**
     * Removes the {@link SessionListenerActivity} instance as a listener from the session when the activity
     * is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.removeListener(this);
    }

}
