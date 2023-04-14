package com.groupseven.musicmap.screens.profile;

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

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.R.id;
import com.groupseven.musicmap.util.adapters.FeedAdapter;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.firebase.Queries;
import com.groupseven.musicmap.util.ui.Message;
import com.squareup.picasso.Picasso;

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
        feedAdapter = new FeedAdapter(activity, R.layout.single_post_layout_feed, false);
        profileListView.setAdapter(feedAdapter);

        Bundle args = getArguments();
        System.out.println("->" + args.getString(Constants.PROFILE_USER_UID_ARGUMENT));
        if (args == null || args.getString(Constants.PROFILE_USER_UID_ARGUMENT) == null) {
            displayData(Session.getInstance().getCurrentUser());
        } else {
            String userUid = args.getString(Constants.PROFILE_USER_UID_ARGUMENT);
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
        Uri uri = user.getData().getProfilePictureUri();
        Picasso.get().load(uri).into(profilePicture);

        Queries.getMusicMemoriesByAuthorId(user.getUid()).whenComplete((feed, throwable) -> {
            if (throwable == null) {
                feedAdapter.addAll(feed);
                feedAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Exception occurred while getting music memories from author", throwable);
            }
        });
    }

}