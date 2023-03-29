package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicmap.R;
import com.example.musicmap.feed.MusicMemory;

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

    public static final String TAG = "MusicMemoryFragment";

    // TODO: Rename and change types of parameters
    private MusicMemory musicMemory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity activity = (HomeActivity) requireActivity();
        this.musicMemory = activity.getCurrentMusicMemory();

        System.out.println(this.musicMemory.getSong());
        System.out.println(this.musicMemory.getPhoto());
        System.out.println(this.musicMemory.getLocation());
        System.out.println(this.musicMemory.getTimePosted());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_music_memory, container, false);
        this.mapView = rootView.findViewById(R.id.post_map);
        return rootView;
    }
}