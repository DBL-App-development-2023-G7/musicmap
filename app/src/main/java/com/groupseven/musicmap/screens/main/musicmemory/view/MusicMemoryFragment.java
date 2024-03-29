package com.groupseven.musicmap.screens.main.musicmemory.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.firebase.Queries;
import com.groupseven.musicmap.util.ui.CircleTransform;
import com.groupseven.musicmap.util.ui.FragmentUtil;
import com.groupseven.musicmap.util.ui.Message;
import com.groupseven.musicmap.util.ui.SpotifyWidgetFragment;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

/**
 * A fragment displaying a single music memory.
 * <p>
 * Requires two string arguments: author UID and post UID.
 */
public class MusicMemoryFragment extends Fragment {

    private static final String TAG = "MusicMemoryFragment";

    private ImageView imageView;
    private ImageView profilePictureView;
    private TextView usernameView;
    private TextView dateView;
    private SpotifyWidgetFragment spotifyWidget;

    // Details about the MusicMemory this fragment is for
    private String authorUid;
    private String musicMemoryUid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            throw new NullPointerException("No arguments provided to MusicMemoryMapFragment");
        }

        musicMemoryUid = args.getString(Constants.MUSIC_MEMORY_UID_ARGUMENT_KEY);
        authorUid = args.getString(Constants.AUTHOR_UID_ARGUMENT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = requireActivity();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_music_memory, container, false);

        if (authorUid == null) {
            return rootView; // no args provided
        }

        // Give arguments to map
        Bundle mapArgs = new Bundle();
        mapArgs.putString(Constants.MUSIC_MEMORY_UID_ARGUMENT_KEY, musicMemoryUid);
        mapArgs.putString(Constants.AUTHOR_UID_ARGUMENT_KEY, authorUid);

        FragmentUtil.replaceFragment(getChildFragmentManager(), R.id.music_memory_map,
                MusicMemoryMapFragment.class, mapArgs);

        /* Assigning all views. */
        this.imageView = rootView.findViewById(R.id.memoryImageView);
        this.profilePictureView = rootView.findViewById(R.id.profile_picture_view);
        this.dateView = rootView.findViewById(R.id.date_text_view);
        this.usernameView = rootView.findViewById(R.id.username_text_view);
        ImageView backButton = rootView.findViewById(R.id.appbarBack);

        this.spotifyWidget = (SpotifyWidgetFragment) getChildFragmentManager().findFragmentById(R.id.spotify_widget);

        /* Back button handling.*/
        backButton.setOnClickListener(task -> activity.onBackPressed());

        // TODO maybe some sort of loading symbol before MusicMemory is loaded

        Queries.getMusicMemoryByAuthorIdAndId(authorUid, musicMemoryUid).whenCompleteAsync((musicMemory, throwable) -> {
            if (throwable != null) {
                Log.e(TAG, "MusicMemory could not be loaded in MusicMemoryFragment", throwable);
                Message.showFailureMessage(container, "Music Memory could not be loaded");
                return;
            }

            spotifyWidget.setupFragment(musicMemory.getSong().getName(),
                    musicMemory.getSong().getArtistName(),
                    musicMemory.getSong().getImageUri().toString(),
                    musicMemory.getSong().getMusicPreviewUri());
            Picasso.get().load(musicMemory.getPhoto()).into(imageView);

            AuthSystem.getUserData(musicMemory.getAuthorUid()).whenCompleteAsync((data, userDataThrowable) -> {
                if (userDataThrowable != null) {
                    Log.e(TAG, "MusicMemory author data could not be loaded in MusicMemoryFragment",
                            userDataThrowable);
                    Message.showFailureMessage(container, "Music Memory author data could not be loaded");
                    return;
                }

                this.usernameView.setText(data.getUsername());

                Picasso.get().load(data.getProfilePictureUri())
                        .transform(new CircleTransform())
                        .into(this.profilePictureView);
            }, ContextCompat.getMainExecutor(requireContext()));
            Date postedDate = musicMemory.getTimePosted();

            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
            this.dateView.setText(getString(R.string.posted_on, dateFormat.format(postedDate)));
            this.spotifyWidget.setSongName(musicMemory.getSong().getName());
            this.spotifyWidget.setArtistName(musicMemory.getSong().getArtistName());
        }, ContextCompat.getMainExecutor(requireContext()));

        return rootView;
    }

}