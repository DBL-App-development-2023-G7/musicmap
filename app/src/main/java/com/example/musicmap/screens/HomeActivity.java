package com.example.musicmap.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

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

    public void logout() {
        auth.signOut();
        Intent loginIntent = new Intent(this, AuthActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);
        finish();
    }

}