package com.example.musicmap.screens.map;

import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.util.map.MusicMemoryOverlay;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class PostMapFragment extends MapFragment {

    private static final MusicMemory MUSIC_MEMORY = new MusicMemory(
            "AUTHOR HERE :)",
            new Date(),
            new GeoPoint(51.446097, 5.486363),
            "https://i.imgur.com/5yeBVeM.jpeg",
            "bazinga"
    );

    @Override
    protected void addOverlays() {
        super.addOverlays();

        addOverlay(new MusicMemoryOverlay(getMapView(), MUSIC_MEMORY));
    }

}
