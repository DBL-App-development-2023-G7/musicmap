package com.groupseven.musicmap.screens.main.map;

import android.util.Log;

import androidx.core.content.ContextCompat;

import com.groupseven.musicmap.util.firebase.Queries;
import com.groupseven.musicmap.util.map.MusicMemoryOverlay;

import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

/**
 * A {@link MapFragment} showing recent posts.
 */
public class PostMapFragment extends MapFragment {

    private static final String TAG = "PostMapFragment";

    /**
     * The posts displayed on this map.
     */
    private final FolderOverlay postsFolder = new FolderOverlay();

    @Override
    protected void addOverlays() {
        super.addOverlays();

        addOverlay(postsFolder);

        updatePosts(); // start adding posts to the map
    }

    private void updatePosts() {
        // Remove all music memories from map
        for (Overlay overlay : new ArrayList<>(postsFolder.getItems())) {
            postsFolder.remove(overlay);
        }

        // Start fetching music memories
        Queries.getAllMusicMemoriesInLastTwentyFourHours().whenCompleteAsync((musicMemories, throwable) -> {
            if (postsFolder.getItems() == null) {
                // Overlay got detached (screen is no longer visible), don't add overlays to map
                return;
            }

            if (throwable == null) {
                // Add all retrieved music memories to map
                musicMemories.stream()
                        .map(musicMemory -> new MusicMemoryOverlay(getMapView(), musicMemory))
                        .forEach(postsFolder::add);

                // Refresh map
                getMapView().invalidate();
            } else {
                Log.e(TAG, "Exception occurred while getting map music memories", throwable);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

}
