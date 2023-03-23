package com.example.musicmap.screens.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.musicmap.R;
import com.example.musicmap.R.id;
import com.example.musicmap.feed.FeedAdapter;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.screens.auth.AuthActivity;
import com.example.musicmap.util.firebase.AuthSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfilePageFragment extends Fragment implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile_page, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.reload().continueWithTask(task -> {
                TextView emailVerified = profileView.findViewById(id.emailVerified_text);
                if (!firebaseUser.isEmailVerified()) {
                    emailVerified.setText(getString(R.string.email_not_verified));
                }

                TextView userName = profileView.findViewById(R.id.profileUsername_textView);
                userName.setText(firebaseUser.getDisplayName()); // TODO get username here

                return null;
            });
            AuthSystem.getUser().addOnSuccessListener(user -> {
                if (user.getData().hasProfilePicture()) {
                    ImageView profilePicture = profileView.findViewById(id.profilePictureImage);
                    Uri uri = user.getData().getProfilePictureUri();
                    Picasso.get().load(uri).into(profilePicture);
                }
            });
        }

        // Recent Music Memories
        List<MusicMemory> musicMemories = new ArrayList<>();

        // TODO replace with actual music memories of the user
        for (int i = 0; i < 3; i++) {
            // CSOFF: LineLength
            String imageUri = "https://www.agconnect.nl/sites/ag/files/2020-05/tu_eindhoven_photo_-_bart_van_overbeeke.jpg.png";
            // CSON: LineLength
            Uri uri = Uri.parse(imageUri);
            musicMemories.add(new MusicMemory(new Date(), new GeoPoint(51.4486, 5.4907), uri));
        }

        Activity activity = requireActivity();
        FeedAdapter feedAdapter = new FeedAdapter(activity, R.layout.single_post_layout_feed, musicMemories);
        ListView profileListView = profileView.findViewById(R.id.mm_list);
        profileListView.setAdapter(feedAdapter);

        // logout button
        Button logoutButton = profileView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> AuthSystem.logout());

        // deleteAccount button
        Button deleteAccountButton = profileView.findViewById(R.id.deleteAccount_button);
        deleteAccountButton.setOnClickListener(view -> AuthSystem.deleteUser());

        return profileView;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            Intent authIntent = new Intent(this.getActivity(), AuthActivity.class);
            authIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(authIntent);
            this.getActivity().finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }
}