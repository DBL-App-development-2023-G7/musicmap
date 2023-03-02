package com.example.musicmap.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        TextView uuidText = findViewById(R.id.uuid_textView);
        TextView emailText = findViewById(R.id.email_textView);
        TextView usernameText = findViewById(R.id.username_textView);

        if (user != null) {
            uuidText.setText(user.getUid());
            emailText.setText(user.getEmail());
            usernameText.setText(user.getDisplayName());
        }

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> logout());
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            Intent authIntent = new Intent(this, AuthActivity.class);
            authIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(authIntent);
            finish();
        }
    }

    public void logout() {
        auth.signOut();
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

}