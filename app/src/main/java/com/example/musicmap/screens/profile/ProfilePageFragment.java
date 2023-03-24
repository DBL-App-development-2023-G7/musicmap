package com.example.musicmap.screens.profile;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.firebase.Queries;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfilePageFragment extends Fragment {

    private TextView emailVerifiedTextView;
    private TextView usernameTextView;
    private ImageView profilePicture;
    private FeedAdapter feedAdapter;
    private static final String TAG = "ProfilePageFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile_page, container, false);

        emailVerifiedTextView = profileView.findViewById(id.emailVerified_text);
        usernameTextView = profileView.findViewById(R.id.profileUsername_textView);
        profilePicture = profileView.findViewById(id.profilePictureImage);
        ListView profileListView = profileView.findViewById(R.id.mm_list);

        // logout button
        Button logoutButton = profileView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> AuthSystem.logout());

        // deleteAccount button
        Button deleteAccountButton = profileView.findViewById(R.id.deleteAccount_button);
        deleteAccountButton.setOnClickListener(view -> AuthSystem.deleteUser());

        Activity activity = requireActivity();
        feedAdapter = new FeedAdapter(activity, R.layout.single_post_layout_feed);
        profileListView.setAdapter(feedAdapter);

        displayData();

        return profileView;
    }

    private void displayData() {
        User currentUser = Session.getInstance().getCurrentUser();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.reload().addOnCompleteListener(task -> {
                if (!firebaseUser.isEmailVerified()) {
                    emailVerifiedTextView.setText(getString(R.string.email_not_verified));
                } else {
                    emailVerifiedTextView.setText(getString(R.string.email_verified));
                }
            });

            if (currentUser != null) {
                usernameTextView.setText(currentUser.getData().getUsername());
                if (currentUser.getData().hasProfilePicture()) {
                    Uri uri = currentUser.getData().getProfilePictureUri();
                    Picasso.get().load(uri).into(profilePicture);
                }
            }

            Queries.getMusicMemoriesByAuthorId(firebaseUser.getUid()).addOnCompleteListener(completedTask -> {
                if (completedTask.isSuccessful()) {
                    List<MusicMemory> feed = completedTask.getResult();
                    feedAdapter.addAll(feed);
                    feedAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, completedTask.getException() == null ? "Unclear error"
                            : completedTask.getException().getMessage());
                }
            });
        }
    }

}