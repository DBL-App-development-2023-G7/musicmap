package com.example.musicmap.feed;

import static androidx.core.content.ContextCompat.startActivity;

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

import com.example.musicmap.R;
import com.example.musicmap.screens.profile.ProfileActivity;
import com.example.musicmap.user.User;
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

    public FeedAdapter(@NonNull Activity activityContext, int resource, @NonNull List<MusicMemory> feedItems) {
        super(activityContext, resource, feedItems);
        this.activityContext = activityContext;
    }

    public FeedAdapter(@NonNull Activity activityContext, int resource) {
        super(activityContext, resource);
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    @SuppressWarnings("unused") // suppressing unused since not all details are set for a music memory yet
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            row = inflater.inflate(R.layout.single_post_layout_feed, parent, false);
        }

        ImageView songImage = row.findViewById(R.id.song_art);
        TextView songName = row.findViewById(R.id.song_name);
        TextView songDetails = row.findViewById(R.id.song_details);
        ImageView memoryImage = row.findViewById(R.id.memory_image);
        ImageView userImage = row.findViewById(R.id.user_profile_image);

        MusicMemory musicMemory = getItem(position);
        if (musicMemory != null) {
            AuthSystem.getUser(musicMemory.getAuthorUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User musicMemoryAuthor = task.getResult();
                    Picasso.get().load(musicMemoryAuthor.getData().getProfilePictureUri())
                            .transform(new CircleTransform()).into(userImage);

                    userImage.setOnClickListener(view -> {
                        Intent intent = new Intent(activityContext, ProfileActivity.class);
                        intent.putExtra("user_uid", musicMemory.getAuthorUid());
                        activityContext.startActivity(intent);
                    });
                } else {
                    Log.e(TAG, "Could not fetch author of the music memory", task.getException());
                }
            });

            songName.setText(musicMemory.getSong().getName());
            Picasso.get().load(musicMemory.getPhoto()).into(memoryImage);
            Picasso.get().load(musicMemory.getSong().getImageUri())
                    .transform(new CircleTransform()).into(songImage);
        }

        return row;
    }

}
