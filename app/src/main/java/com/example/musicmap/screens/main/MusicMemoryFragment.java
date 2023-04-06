package com.example.musicmap.screens.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.musicmap.R;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.screens.map.MusicMemoryMapFragment;
import com.example.musicmap.user.UserData;
import com.example.musicmap.util.Constants;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.firebase.Queries;
import com.example.musicmap.util.ui.CircleTransform;
import com.example.musicmap.util.ui.FragmentUtil;
import com.example.musicmap.util.ui.Message;
import com.squareup.picasso.Picasso;

import org.osmdroid.config.Configuration;

import java.text.DateFormat;
import java.util.Date;

/**
 * A fragment displaying a single music memory.
 *
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
            throw new IllegalArgumentException("No arguments provided to MusicMemoryFragment");
        }

        musicMemoryUid = args.getString(Constants.MUSIC_MEMORY_UID_ARGUMENT_KEY);
        authorUid = args.getString(Constants.AUTHOR_UID_ARGUMENT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = requireActivity();

        Context ctx = activity.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_music_memory, container, false);

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

        Queries.getMusicMemoryByAuthorIdAndId(authorUid, musicMemoryUid).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "MusicMemory could not be loaded in MusicMemoryFragment", task.getException());
                Message.showFailureMessage(container, "Music Memory could not be loaded");
                return;
            }
            
            MusicMemory musicMemory = task.getResult();
            spotifyWidget.setupFragment(musicMemory.getSong().getName(),
                    musicMemory.getSong().getSpotifyArtistId(),
                    musicMemory.getSong().getImageUri().toString());
            Picasso.get().load(musicMemory.getPhoto()).into(imageView);

            AuthSystem.getUserData(musicMemory.getAuthorUid()).addOnCompleteListener(userDataTask -> {
                if (!userDataTask.isSuccessful()) {
                    Log.e(TAG, "MusicMemory author data could not be loaded in MusicMemoryFragment",
                            userDataTask.getException());
                    Message.showFailureMessage(container, "Music Memory author data could not be loaded");
                    return;
                }

                UserData data = userDataTask.getResult();

                this.usernameView.setText(data.getUsername());

                Picasso.get().load(data.getProfilePictureUri())
                        .transform(new CircleTransform())
                        .into(this.profilePictureView);
            });
            Date postedDate = musicMemory.getTimePosted();

            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
            this.dateView.setText(getString(R.string.posted_on, dateFormat.format(postedDate)));
            this.spotifyWidget.setSongName(musicMemory.getSong().getName());
            this.spotifyWidget.setArtistName(musicMemory.getSong().getSpotifyArtistId());
        });

        return rootView;
    }

}