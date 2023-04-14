package com.groupseven.musicmap.screens.main.musicmemory.view;

import android.os.Bundle;

import com.groupseven.musicmap.screens.main.map.MapFragment;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.firebase.Queries;
import com.groupseven.musicmap.util.map.MusicMemoryOverlay;

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

        String authorUid = args.getString(Constants.AUTHOR_UID_ARGUMENT_KEY);
        String musicMemoryUid = args.getString(Constants.MUSIC_MEMORY_UID_ARGUMENT_KEY);

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

    @Override
    protected boolean allowInteraction() {
        return false;
    }

}