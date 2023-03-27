package com.example.musicmap.screens.map;

import android.util.Log;

import com.example.musicmap.util.firebase.Queries;
import com.example.musicmap.util.map.MusicMemoryOverlay;

import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A map showing posts (music memories etc).
 */
public class PostMapFragment extends MapFragment {

    private static final String TAG = "PostMapFragment";

    private final FolderOverlay postsFolder = new FolderOverlay();
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    @Override
    protected void addOverlays() {
        super.addOverlays();
        addOverlay(postsFolder);
        updatePosts();
    }

    /**
     * Updates the posts displayed on the map, possibly adding new posts.
     */
    public void update() {
        if (isUpdating.getAndSet(true)) {
            updatePosts();
            isUpdating.set(false);
        }
    }

    private void updatePosts() {
        // Remove all music memories from map
        for (Overlay overlay : new ArrayList<>(postsFolder.getItems())) {
            postsFolder.remove(overlay);
        }

        // Start fetching music memories
        Queries.getAllMusicMemories().addOnCompleteListener(completedTask -> {
            if (completedTask.isSuccessful()) {
                // Add all retrieved music memories to map
                completedTask.getResult().stream()
                        .map(musicMemory -> new MusicMemoryOverlay(getMapView(), musicMemory))
                        .forEach(postsFolder::add);

                // Refresh map
                getMapView().invalidate();
            } else {
                Log.e(TAG, "Exception occurred while getting map music memories", completedTask.getException());
            }
        });
    }

}
