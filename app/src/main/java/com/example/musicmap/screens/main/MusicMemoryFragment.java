package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicmap.R;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.user.UserData;
import com.example.musicmap.util.firebase.AuthSystem;
import com.squareup.picasso.Picasso;

import org.osmdroid.views.MapView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicMemoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicMemoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String song;
    private String authorUID;
    private String photoURI;
    private MapView mapView;
    private ImageView imageView;
    private ImageView profilePictureView;
    private TextView usernameView;
    private TextView dateView;
    private ImageView backButton;

    public static final String TAG = "MusicMemoryFragment";

    // TODO: Rename and change types of parameters
    private MusicMemory musicMemory;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity activity = (HomeActivity) requireActivity();
        this.musicMemory = activity.getCurrentMusicMemory();
        System.out.println(this.musicMemory.getSong());
        System.out.println(this.musicMemory.getLocation());
        System.out.println(this.musicMemory.getTimePosted());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_music_memory, container, false);

        /* Assigning all views. */
        this.mapView = rootView.findViewById(R.id.post_map);
        this.imageView = rootView.findViewById(R.id.memoryImageView);
        this.profilePictureView = rootView.findViewById(R.id.profile_picture_view);
        this.dateView = rootView.findViewById(R.id.date_text_view);
        this.usernameView = rootView.findViewById(R.id.username_text_view);
        this.backButton = rootView.findViewById(R.id.appbarBack);

        /* Putting in all the data. */
        Picasso.get().load(this.musicMemory.getPhoto()).into(imageView);
        AuthSystem.getUserData(this.musicMemory.getAuthorUid()).addOnCompleteListener(task -> {
            UserData data = task.getResult();
            this.usernameView.setText(data.getUsername());
            System.out.println(data.getProfilePicture() + "|| " + data.getProfilePictureUri());
            Picasso.get().load(data.getProfilePictureUri()).into(this.profilePictureView);
        });
        this.dateView.setText(this.musicMemory.getTimePosted().toString());
        return rootView;
    }
}