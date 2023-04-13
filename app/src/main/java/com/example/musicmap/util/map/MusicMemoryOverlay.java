package com.example.musicmap.util.map;

import android.content.Context;
import android.content.Intent;

import com.example.musicmap.models.MusicMemory;
import com.example.musicmap.screens.main.musicmemory.MusicMemoryActivity;
import com.example.musicmap.util.Constants;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;

/**
 * A {@link PostOverlay} for music memories.
 */
public class MusicMemoryOverlay extends PostOverlay<MusicMemory> {

    public MusicMemoryOverlay(MapView mapView, MusicMemory musicMemory) {
        super(mapView, musicMemory, musicMemory.getPhoto());
    }

    @Override
    protected boolean onMarkerClicked(MapView mapView, int markerId, IGeoPoint makerPosition, MusicMemory markerData) {
        Context context = mapView.getContext();

        Intent intent = new Intent(context, MusicMemoryActivity.class);
        intent.putExtra(Constants.AUTHOR_UID_ARGUMENT_KEY, getPost().getAuthorUid());
        intent.putExtra(Constants.MUSIC_MEMORY_UID_ARGUMENT_KEY, getPost().getUid());
        intent.putExtra(Constants.IS_SENT_FROM_FEED_ARGUMENT_KEY, true);

        context.startActivity(intent);

        return true;
    }

}
