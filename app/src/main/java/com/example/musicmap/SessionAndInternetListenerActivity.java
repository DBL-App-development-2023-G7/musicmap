package com.example.musicmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.screens.NoInternetActivity;
import com.example.musicmap.screens.auth.AuthActivity;
import com.example.musicmap.screens.verification.VerificationActivity;
import com.example.musicmap.user.Artist;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.Constants;

public class SessionAndInternetListenerActivity extends AppCompatActivity implements Session.Listener {

    protected Session session;

    private final BroadcastReceiver internetCheckReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTERNET_BROADCAST_ACTION)) {
                boolean isInternetAvailable = intent.getBooleanExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, true);
                if (!isInternetAvailable) {
                    startActivity(new Intent(SessionAndInternetListenerActivity.this, NoInternetActivity.class));
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = Session.getInstance();
        session.addListener(this);

        IntentFilter intentFilter = new IntentFilter(Constants.INTERNET_BROADCAST_ACTION);
        registerReceiver(internetCheckReceiver, intentFilter);
    }

    @Override
    public void onSessionStateChanged() {
        if (!session.isUserConnected()) {
            loadAuthActivity();
        }
        if (session.isUserLoaded()) {
            User currentUser = session.getCurrentUser();
            if (currentUser.isArtist() && !((Artist) currentUser).isVerified()) {
                loadVerificationActivity();
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
        unregisterReceiver(internetCheckReceiver);
    }

}
