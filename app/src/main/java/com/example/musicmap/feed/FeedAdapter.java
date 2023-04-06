package com.example.musicmap.feed;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicmap.R;
import com.example.musicmap.screens.main.HomeActivity;
import com.example.musicmap.screens.main.MusicMemoryFragment;
import com.example.musicmap.util.ui.FragmentUtil;
import com.example.musicmap.screens.profile.ProfileActivity;
import com.example.musicmap.user.User;
import com.example.musicmap.util.Constants;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The adapter providing data for the feed.
 */
public class FeedAdapter extends ArrayAdapter<MusicMemory> {

    private final Activity activityContext;
    private static final String TAG = "FeedAdapter";
    private boolean isUsedInFeed = true;

    public FeedAdapter(@NonNull Activity activityContext, int resource, @NonNull List<MusicMemory> feedItems) {
        super(activityContext, resource, feedItems);
        this.activityContext = activityContext;
    }

    public FeedAdapter(@NonNull Activity activityContext, int resource, boolean isUsedInFeed) {
        super(activityContext, resource);
        this.activityContext = activityContext;
        this.isUsedInFeed = isUsedInFeed;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        View row = convertView;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            row = inflater.inflate(R.layout.single_post_layout_feed, parent, false);
        }

        MusicMemory musicMemory = getItem(position);
        if (musicMemory == null) {
            return row;
        }

        HomeActivity homeActivity = (HomeActivity) activityContext;
        ImageView songImage = row.findViewById(R.id.song_art);
        TextView songName = row.findViewById(R.id.song_name);
        TextView songDetails = row.findViewById(R.id.song_details);
        ImageView memoryImage = row.findViewById(R.id.memory_image);
        ImageView userImage = row.findViewById(R.id.user_profile_image);

        row.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString(Constants.AUTHOR_UID_ARGUMENT_KEY, musicMemory.getAuthorUid());
            args.putString(Constants.MUSIC_MEMORY_UID_ARGUMENT_KEY, musicMemory.getUid());

            FragmentUtil.replaceFragment(homeActivity.getFragmentManagerFromActivity(), R.id.fragment_view,
                    MusicMemoryFragment.class, args);

            homeActivity.hideBottomNav();
            homeActivity.hideTopNav();
        });

        if (isUsedInFeed) {
            AuthSystem.getUser(musicMemory.getAuthorUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User musicMemoryAuthor = task.getResult();
                    String userImageUri = musicMemoryAuthor.getData().getProfilePictureUri().toString();
                    Picasso.get().load(userImageUri).transform(new CircleTransform()).into(userImage);

                    userImage.setVisibility(View.VISIBLE);
                    userImage.setOnClickListener(view -> {
                        Intent intent = new Intent(activityContext, ProfileActivity.class);
                        intent.putExtra(Constants.PROFILE_USER_UID_ARGUMENT, musicMemory.getAuthorUid());
                        activityContext.startActivity(intent);
                    });
                } else {
                    Log.e(TAG, "Could not fetch author of the music memory", task.getException());
                }
            });
        } else {
            userImage.setVisibility(View.INVISIBLE);
        }

        songName.setText(musicMemory.getSong().getName());
        Picasso.get().load(musicMemory.getPhoto()).into(memoryImage);
        Picasso.get().load(musicMemory.getSong().getImageUri()).transform(new CircleTransform()).into(songImage);
        return row;
    }

}
