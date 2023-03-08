package com.example.musicmap.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.auth.AuthActivity;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.FragmentUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);

        FirebaseUser firebaseUser = auth.getCurrentUser();

        TextView emailVerified = findViewById(R.id.emailVerified_textView);
        TextView uuidText = findViewById(R.id.uuid_textView);
        TextView emailText = findViewById(R.id.email_textView);
        TextView usernameText = findViewById(R.id.username_textView);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (firebaseUser != null) {
            firebaseUser.reload().continueWithTask(task -> {
                runOnUiThread(() -> {
                    if (firebaseUser.isEmailVerified()) {
                        emailVerified.setText(getString(R.string.email_verified));
                    } else {
                        emailVerified.setText(R.string.email_not_verified);
                    }
                    uuidText.setText(firebaseUser.getUid());
                    emailText.setText(firebaseUser.getEmail());
                    usernameText.setText(firebaseUser.getDisplayName());
                });
                return null;
            });
        }

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> logout());

        Button deleteAccountButton = findViewById(R.id.deleteAccount_button);
        deleteAccountButton.setOnClickListener(view -> deleteAccount());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // using ifs instead of switch as resource IDs will be non-final by default in
            // Android Gradle Plugin version 8.0, therefore not to be used in switch
            if (item.getItemId() == R.id.navbarFeed) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        FeedFragment.class);
                return true;
            }

            if (item.getItemId() == R.id.navbarPost) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        PostFragment.class);
                return true;
            }

            return false;
        });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            Intent authIntent = new Intent(this, AuthActivity.class);
            authIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(authIntent);
            finish();
        }
    }

    public void logout() {
        auth.signOut();
    }

    // TODO refactor, should be elsewhere
    public void deleteAccount() {
        AuthSystem.deleteUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

}