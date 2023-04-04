package com.example.musicmap.screens.profile;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.musicmap.util.ui.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfilePageFragment extends Fragment {

    private static final String TAG = "ProfilePageFragment";

    private TextView usernameTextView;
    private ImageView profilePicture;
    private FeedAdapter feedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile_page, container, false);

        usernameTextView = profileView.findViewById(R.id.profileUsername_textView);
        profilePicture = profileView.findViewById(id.profilePictureImage);
        ListView profileListView = profileView.findViewById(R.id.mm_list);

        Activity activity = requireActivity();
        feedAdapter = new FeedAdapter(activity, R.layout.single_post_layout_feed);
        profileListView.setAdapter(feedAdapter);

        Bundle args = getArguments();
        if (args == null || args.getString("user_uid") == null) {
            displayData(Session.getInstance().getCurrentUser());
        } else {
            String userUid = args.getString("user_uid");
            AuthSystem.getUser(userUid).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Exception occurred while getting user data for profile", task.getException());
                    Message.showFailureMessage(container, "Could not load user data");
                    return;
                }

                displayData(task.getResult());
            });
        }

        return profileView;
    }

    private void displayData(User user) {
        if (user == null) {
            return;
        }

        usernameTextView.setText(user.getData().getUsername());
        if (user.getData().hasProfilePicture()) {
            Uri uri = user.getData().getProfilePictureUri();
            Picasso.get().load(uri).into(profilePicture);
        }

        Queries.getMusicMemoriesByAuthorId(user.getUid()).addOnCompleteListener(completedTask -> {
            if (completedTask.isSuccessful()) {
                List<MusicMemory> feed = completedTask.getResult();
                feedAdapter.addAll(feed);
                feedAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Exception occurred while getting music memories from author",
                        completedTask.getException());
            }
        });
    }

}