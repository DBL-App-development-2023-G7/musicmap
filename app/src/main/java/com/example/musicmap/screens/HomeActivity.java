package com.example.musicmap.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.musicmap.R;
import com.example.musicmap.screens.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        TextView uuidText = (TextView) findViewById(R.id.uuid_textView);
        TextView emailText = (TextView) findViewById(R.id.email_textView);
        TextView usernameText = (TextView) findViewById(R.id.username_textView);

        if (user != null) {
            uuidText.setText(user.getUid());
            emailText.setText(user.getEmail());
            usernameText.setText(user.getDisplayName());
        }

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    public void logout() {
        auth.signOut();
        Intent loginIntent = new Intent(this, AuthActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);
        finish();
    }
}