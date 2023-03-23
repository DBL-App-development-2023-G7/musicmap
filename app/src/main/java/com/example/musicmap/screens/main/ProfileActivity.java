package com.example.musicmap.screens.main;

import android.os.Bundle;

import com.example.musicmap.AuthListenerActivity;
import com.example.musicmap.R;

public class ProfileActivity extends SessionListenerActivity {
    private ImageView profilePicture;
    private TextView uuidText;
    private TextView emailText;
    private TextView usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    public void onSessionStateChanged() {
        super.onSessionStateChanged();
        displayData();
    }

    private void displayData() {
        User currentUser = Session.getInstance().getCurrentUser();

        if (currentUser != null) {
            uuidText.setText(currentUser.getUid());
            emailText.setText(currentUser.getData().getEmail());
            usernameText.setText(currentUser.getData().getUsername());
            if (currentUser.getData().hasProfilePicture()) {
                Log.d("Test", "merghe");
                Uri uri = currentUser.getData().getProfilePictureUri();
                Picasso.get().load(uri).into(profilePicture);
            }
        }
    }

}