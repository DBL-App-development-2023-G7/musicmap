package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicmap.R;
import com.example.musicmap.feed.MusicMemory;

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

    public static final String TAG = "MusicMemoryFragment";

    // TODO: Rename and change types of parameters
    private MusicMemory musicMemory;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MusicMemoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicMemoryFragment newInstance(MusicMemory mm) {
        MusicMemoryFragment fragment = new MusicMemoryFragment();
        Bundle args = new Bundle();
        args.putString("song", mm.getSong());
        args.putString("photoURL", mm.getPhoto().toString());
        args.putString("authorUID", mm.getAuthorUid());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity activity = (HomeActivity) requireActivity();
        this.musicMemory = activity.getCurrentMusicMemory();

        if (getArguments() != null) {
            this.song = getArguments().getString("song");
            this.photoURI = getArguments().getString("photoURL");
            this.authorUID = getArguments().getString("authorUID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_memory, container, false);
    }
}