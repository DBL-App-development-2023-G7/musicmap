package com.example.musicmap.screens.verification;

import android.os.Bundle;
import android.widget.Button;

import com.example.musicmap.R;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.AuthListenerActivity;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

public class VerificationActivity extends AuthListenerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Button signOutVerificationButton = findViewById(R.id.signout_verification_button);
        signOutVerificationButton.setOnClickListener(view -> signOut());
    }

    private void signOut() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
    }

}