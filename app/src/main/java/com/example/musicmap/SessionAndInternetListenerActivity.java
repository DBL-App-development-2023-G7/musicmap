package com.example.musicmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.screens.auth.AuthActivity;
import com.example.musicmap.screens.verification.VerificationActivity;
import com.example.musicmap.models.Artist;
import com.example.musicmap.firebase.Session;
import com.example.musicmap.models.User;
import com.example.musicmap.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class SessionAndInternetListenerActivity extends AppCompatActivity implements Session.Listener {

    private Session session;

    private final BroadcastReceiver internetCheckReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTERNET_BROADCAST_ACTION)) {
                boolean internetAvailable = intent.getBooleanExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, true);
                updateLayout(internetAvailable);
            }
        }
    };

    /**
     * Abstract method that the child activities must override, to dynamically switch the layout
     * from the activity/fragment to layout for no internet.
     *
     * @param internetAvailable true if internet connection available, false otherwise.
     */
    protected abstract void updateLayout(boolean internetAvailable);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = Session.getInstance();
        session.addListener(this);

        IntentFilter intentFilter = new IntentFilter(Constants.INTERNET_BROADCAST_ACTION);
        registerReceiver(internetCheckReceiver, intentFilter);
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
        unregisterReceiver(internetCheckReceiver);
    }

}
