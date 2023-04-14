package com.groupseven.musicmap.util.adapters;

import android.app.Activity;
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

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.screens.main.musicmemory.view.MusicMemoryActivity;
import com.groupseven.musicmap.screens.profile.ProfileActivity;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.ui.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The adapter providing data for the feed.
 */
public class FeedAdapter extends ArrayAdapter<MusicMemory> {

    private final Activity activityContext;
    private static final String TAG = "FeedAdapter";
    private boolean isUsedInFeed = true;
    private final Map<String, String> userImageByAuthorIdMap = new HashMap<>();

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
            row.findViewById(R.id.user_profile_image).setTag(null);
        } else {
            Picasso.get().cancelRequest((ImageView) convertView.findViewById(R.id.user_profile_image));
        }

        MusicMemory musicMemory = getItem(position);
        if (musicMemory == null) {
            return row;
        }

        row.setOnClickListener(v -> {
            Intent intent = new Intent(activityContext, MusicMemoryActivity.class);
            intent.putExtra(Constants.AUTHOR_UID_ARGUMENT_KEY, musicMemory.getAuthorUid());
            intent.putExtra(Constants.MUSIC_MEMORY_UID_ARGUMENT_KEY, musicMemory.getUid());
            intent.putExtra(Constants.IS_SENT_FROM_FEED_ARGUMENT_KEY, isUsedInFeed);

            activityContext.startActivity(intent);
            activityContext.finish();
        });

        ImageView songImage = row.findViewById(R.id.song_art);
        TextView songName = row.findViewById(R.id.song_name);
        TextView songDetails = row.findViewById(R.id.song_details);
        ImageView memoryImage = row.findViewById(R.id.memory_image);
        ImageView userImage = row.findViewById(R.id.user_profile_image);

        if (!isUsedInFeed) {
            userImage.setVisibility(View.INVISIBLE);
        } else {
            String userImageUriFromMap = userImageByAuthorIdMap.get(musicMemory.getAuthorUid());

            if (userImageUriFromMap == null) {
                fetchUserImage(musicMemory.getAuthorUid(), userImage, position);
            } else {
                setUserImage(musicMemory.getAuthorUid(), userImageUriFromMap, userImage);
            }
        }

        songName.setText(musicMemory.getSong().getName());
        Picasso.get().load(musicMemory.getPhoto()).into(memoryImage);
        Picasso.get().load(musicMemory.getSong().getImageUri()).transform(new CircleTransform()).into(songImage);
        songDetails.setText(musicMemory.getSong().getArtistName());
        userImage.setTag(position);
        return row;
    }

    private void fetchUserImage(String authorId, ImageView userImage, int position) {
        AuthSystem.getUser(authorId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User musicMemoryAuthor = task.getResult();
                String userImageUri = musicMemoryAuthor.getData().getProfilePictureUri().toString();

                if (userImage.getTag() != null && userImage.getTag().equals(position)) {
                    setUserImage(authorId, userImageUri, userImage);
                }
            } else {
                Log.e(TAG, "Could not fetch author of the music memory", task.getException());
            }
        });
    }

    private void setUserImage(String authorId, String userImageUri, ImageView userImage) {
        Picasso.get().load(userImageUri).transform(new CircleTransform()).into(userImage);
        userImageByAuthorIdMap.put(authorId, userImageUri);

        userImage.setVisibility(View.VISIBLE);
        userImage.setOnClickListener(view -> {
            Intent intent = new Intent(activityContext, ProfileActivity.class);
            intent.putExtra(Constants.PROFILE_USER_UID_ARGUMENT, authorId);
            activityContext.startActivity(intent);
        });
    }

}