package com.example.musicmap.util.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.screens.auth.AuthActivity;
import com.example.musicmap.screens.verification.VerificationActivity;
import com.example.musicmap.user.Artist;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthListenerActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            loadAuthActivity();
        } else {
            AuthSystem.getUser().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult();
                    if (user.isArtist() && !((Artist) user).isVerified()) {
                        loadVerificationActivity();
                    }
                } else {
//                    Exception exception = task.getException();
//                    if (exception != null) {
//                        Log.e(TAG, exception.toString());
//                    } else {
//                        Log.e(TAG, "Unknown error!");
//                    }
                }
            });
        }
    }

    public void loadVerificationActivity() {
        Intent verificationIntent = new Intent(this, VerificationActivity.class);
        verificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(verificationIntent);
        finish();
    }

    private void loadAuthActivity() {
        Intent authIntent = new Intent(this, AuthActivity.class);
        authIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(authIntent);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        auth.removeAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }
}
