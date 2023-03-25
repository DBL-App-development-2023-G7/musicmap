package com.example.musicmap.util.map;

import com.example.musicmap.feed.MusicMemory;

import org.osmdroid.views.MapView;

/**
 * A {@link PostOverlay} for music memories.
 */
public class MusicMemoryOverlay extends PostOverlay {

    public MusicMemoryOverlay(MapView mapView, MusicMemory musicMemory) {
        // TODO change to song photo?
        super(mapView, musicMemory, musicMemory.getPhoto());
    }

}
