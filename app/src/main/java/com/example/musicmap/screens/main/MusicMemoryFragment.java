package com.example.musicmap.screens.main;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.FragmentUtil;
import com.squareup.picasso.Picasso;

import org.osmdroid.config.Configuration;

/**
 * A fragment displaying a single music memory.
 *
 * Requires two string arguments: author UID and post UID.
 */
public class MusicMemoryFragment extends Fragment {

    private static final String TAG = "MusicMemoryFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String song;
    private String authorUID;
    private String photoURI;
    private ImageView imageView;
    private ImageView profilePictureView;
    private TextView usernameView;
    private TextView dateView;
    private ImageView backButton;
    private TextView songAuthorView;
    private TextView songNameView;
    private ImageView songPictureView;

    // TODO: Rename and change types of parameters
    private MusicMemory musicMemory;
    private HomeActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (HomeActivity) requireActivity();
        activity.hideBottomNav();
        this.musicMemory = activity.getCurrentMusicMemory();
        System.out.println(this.musicMemory.getSong());
        System.out.println(this.musicMemory.getLocation());
        System.out.println(this.musicMemory.getTimePosted());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments(); // TODO actually provide arguments
        if (args == null) {
            throw new IllegalArgumentException("No arguments provided to MusicMemoryFragment");
        }

        String musicMemoryUid = args.getString("music_memory_uid");
        String authorUid = args.getString("author_uid");

        Context ctx = activity.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_music_memory, container, false);

        // Give arguments to map
        Bundle mapArgs = new Bundle();
        mapArgs.putString("music_memory_uid", musicMemoryUid);
        mapArgs.putString("author_uid", authorUid);

        FragmentUtil.replaceFragment(getChildFragmentManager(), R.id.music_memory_map,
                MusicMemoryMapFragment.class, mapArgs);

        /* Assigning all views. */
        this.imageView = rootView.findViewById(R.id.memoryImageView);
        this.profilePictureView = rootView.findViewById(R.id.profile_picture_view);
        this.dateView = rootView.findViewById(R.id.date_text_view);
        this.usernameView = rootView.findViewById(R.id.username_text_view);
        this.backButton = rootView.findViewById(R.id.appbarBack);
        this.songAuthorView = rootView.findViewById(R.id.song_author_view);
        this.songNameView = rootView.findViewById(R.id.song_name_view);
        this.songPictureView = rootView.findViewById(R.id.song_picture_view);

        /* Putting in all the data. */
        Picasso.get().load(this.musicMemory.getPhoto()).into(imageView);
        this.songAuthorView.setText(this.musicMemory.getSong().getSpotifyAristId());
        this.songNameView.setText(this.musicMemory.getSong().getName());
        System.out.println(this.musicMemory.getSong().getImageUri());

        AuthSystem.getUserData(this.musicMemory.getAuthorUid()).addOnCompleteListener(task -> {
            UserData data = task.getResult();
            this.usernameView.setText(data.getUsername());
            System.out.println(data.getProfilePicture() + "|| " + data.getProfilePictureUri());
            Picasso.get().load(data.getProfilePictureUri()).into(this.profilePictureView);
            Picasso.get().load(this.musicMemory.getSong().getImageUri()).into(this.songPictureView);
        });
        this.dateView.setText(this.musicMemory.getTimePosted().toString());

        /* Back button handling.*/
        this.backButton.setOnClickListener(task -> this.activity.onBackPressed());

        return rootView;
    }

}