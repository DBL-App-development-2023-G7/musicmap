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
import androidx.core.content.ContextCompat;

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

    /**
     * The tag used to log errors or warnings from this class.
     */
    private static final String TAG = "FeedAdapter";

    /**
     * The context of the Activity where the adapter is being used.
     */
    private final Activity activityContext;

    /**
     * The flag to check if the adapter is being used in feed or profile view.
     */
    private boolean isUsedInFeed = true;

    /**
     * A simple cache for storing profile picture URLs by user UIDs,
     * to avoid accessing the database for it.
     */
    private final Map<String, String> userImageByAuthorIdMap = new HashMap<>();

    /**
     * Constructor for creating the FeedAdapter object.
     *
     * @param activityContext The context of the Activity where the adapter is being used.
     * @param resource The resource ID for the layout file containing the layout for each list item.
     * @param feedItems The list of music memories to be displayed in the list.
     */
    public FeedAdapter(@NonNull Activity activityContext, int resource, @NonNull List<MusicMemory> feedItems) {
        super(activityContext, resource, feedItems);
        this.activityContext = activityContext;
    }

    /**
     * Constructor for creating the FeedAdapter object.
     *
     * @param activityContext The context of the Activity where the adapter is being used.
     * @param resource The resource ID for the layout file containing the layout for each list item.
     * @param isUsedInFeed The flag used to check if adapter is being used in feed or profile view.
     */
    public FeedAdapter(@NonNull Activity activityContext, int resource, boolean isUsedInFeed) {
        super(activityContext, resource);
        this.activityContext = activityContext;
        this.isUsedInFeed = isUsedInFeed;
    }

    /**
     * This method returns the view for each list item.
     *
     * @param position The position of the list item.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view that this view will eventually be attached to.
     * @return The view for each list item.
     */
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

    /**
     * Fetch the user image of the music memory author, and
     * {@link #setUserImage(String, String, ImageView) set the image} afterwards.
     *
     * @param authorId The id of the author of the music memory.
     * @param userImage The imageView to set the image to.
     * @param position The position of the item in the current view.
     */
    private void fetchUserImage(String authorId, ImageView userImage, int position) {
        AuthSystem.getUser(authorId).whenCompleteAsync((musicMemoryAuthor, throwable) -> {
            if (throwable == null) {
                String userImageUri = musicMemoryAuthor.getData().getProfilePictureUri().toString();

                if (userImage.getTag() != null && userImage.getTag().equals(position)) {
                    setUserImage(authorId, userImageUri, userImage);
                }
            } else {
                Log.e(TAG, "Could not fetch author of the music memory", throwable);
            }
        }, ContextCompat.getMainExecutor(userImage.getContext()));
    }

    /**
     * This method is used to set the user image of the music memory author.
     *
     * @param authorId The id of the author of the music memory.
     * @param userImageUri The uri of the image.
     * @param userImage The imageView to set the image to.
     */
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
