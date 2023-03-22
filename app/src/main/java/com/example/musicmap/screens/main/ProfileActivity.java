package com.example.musicmap.screens.main;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicmap.R;
import com.example.musicmap.SessionListenerActivity;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends SessionListenerActivity {

    private FirebaseAuth auth;

    private TextView uuidText;
    private TextView emailText;
    private TextView usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        ImageView profilePicture = findViewById(R.id.profile_imageView);
        TextView emailVerified = findViewById(R.id.emailVerified_textView);
        uuidText = findViewById(R.id.uuid_textView);
        emailText = findViewById(R.id.email_textView);
        usernameText = findViewById(R.id.username_textView);

        if (firebaseUser != null) {
            firebaseUser.reload().continueWithTask(task -> {
                if (firebaseUser.isEmailVerified()) {
                    emailVerified.setText(getString(R.string.email_verified));
                } else {
                    emailVerified.setText(R.string.email_not_verified);
                }

                User currentUser = Session.getInstance().getCurrentUser();

                uuidText.setText(currentUser.getUid());
                emailText.setText(currentUser.getData().getEmail());
                usernameText.setText(currentUser.getData().getUsername());
                return null;
            });
            AuthSystem.getUser().onSuccessTask(user -> {
                if (user.getData().hasProfilePicture()) {
                    Uri uri = user.getData().getProfilePictureUri();
                    Picasso.get().load(uri).into(profilePicture);
                }
                return null;
            });
        }

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> AuthSystem.logout());

        Button deleteAccountButton = findViewById(R.id.deleteAccount_button);
        deleteAccountButton.setOnClickListener(view -> AuthSystem.deleteUser());
    }

    @Override
    public void onSessionStateChanged() {
        super.onSessionStateChanged();

        User currentUser = Session.getInstance().getCurrentUser();

        if (currentUser != null) {
            uuidText.setText(currentUser.getUid());
            emailText.setText(currentUser.getData().getEmail());
            usernameText.setText(currentUser.getData().getUsername());
        }
    }
}