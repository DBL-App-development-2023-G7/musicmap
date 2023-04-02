package com.example.musicmap.screens.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicmap.R;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.user.UserData;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.FragmentUtil;
import com.squareup.picasso.Picasso;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

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
        Context ctx = activity.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
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

        /* MapView handling */
        double latitude = this.musicMemory.getLocation().getLatitude();
        double longitude = this.musicMemory.getLocation().getLongitude();
        mapView.setMinZoomLevel(4.0);

        GeoPoint location = new GeoPoint(latitude, longitude);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        Marker locationMarker = new Marker(mapView);
        locationMarker.setPosition(location);
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(locationMarker);
        mapView.getController().setCenter(location);

        /* Backbutton handling.*/
        this.backButton.setOnClickListener(task->{
                this.activity.onBackPressed();
        }
        );
        return rootView;
    }

}