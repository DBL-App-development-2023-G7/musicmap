package com.example.musicmap.screens.map;

import android.os.Bundle;

import com.example.musicmap.util.firebase.Queries;
import com.example.musicmap.util.map.MusicMemoryOverlay;

/**
 * A map fragment for displaying the location of a music memory.
 *
 * Requires two arguments: author UID and post UID.
 */
public class MusicMemoryMapFragment extends MapFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            // No arguments provided, can't display a music memory
            return;
        }

        String authorUid = args.getString("author_uid");
        String musicMemoryUid = args.getString("music_memory_uid");

        // Get MusicMemory from UIDs
        // TODO MusicMemoryOverlay or just a marker?
        //  mb same marker but without image
        Queries.getMusicMemoryByAuthorIdAndId(authorUid, musicMemoryUid).addOnSuccessListener(musicMemory ->
                addOverlay(new MusicMemoryOverlay(getMapView(), musicMemory)));
    }

    @Override
    protected boolean shouldDisplayCurrentLocation() {
        return false;
    }

    // TODO after merge of https://github.com/DBL-App-development-2023-G7/musicmap/pull/59
//    @Override
    protected boolean allowInteraction() {
        return false;
    }

}
