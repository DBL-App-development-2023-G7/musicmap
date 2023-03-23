package com.example.musicmap.screens.profile;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.musicmap.R;
import com.example.musicmap.R.id;
import com.example.musicmap.feed.FeedAdapter;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.util.firebase.AuthSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfilePageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile_page, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
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
            musicMemories.add(new MusicMemory("You", new Date(), new GeoPoint(51.4486, 5.4907), imageUri, "abc"));
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

}