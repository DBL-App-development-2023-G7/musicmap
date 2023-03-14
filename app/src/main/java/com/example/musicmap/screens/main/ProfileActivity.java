package com.example.musicmap.screens.main;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.auth.AuthActivity;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.MMActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends MMActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        ImageView profilePicture = findViewById(R.id.profile_imageView);
        profilePicture.setOnClickListener(view -> {

        });

        TextView emailVerified = findViewById(R.id.emailVerified_textView);
        TextView uuidText = findViewById(R.id.uuid_textView);
        TextView emailText = findViewById(R.id.email_textView);
        TextView usernameText = findViewById(R.id.username_textView);

        if (firebaseUser != null) {
            firebaseUser.reload().continueWithTask(task -> {
                if (firebaseUser.isEmailVerified()) {
                    emailVerified.setText(getString(R.string.email_verified));
                } else {
                    emailVerified.setText(R.string.email_not_verified);
                }
                uuidText.setText(firebaseUser.getUid());
                emailText.setText(firebaseUser.getEmail());
                usernameText.setText(firebaseUser.getDisplayName());
                return null;
            });
            AuthSystem.getUser().onSuccessTask(user -> {
                if (!user.getData().getProfilePicture().equals("")) {
                    Uri uri = Uri.parse("https://www.agconnect.nl/sites/ag/files/2020-05/tu_eindhoven_photo_-_bart_van_overbeeke.jpg.png");
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

}