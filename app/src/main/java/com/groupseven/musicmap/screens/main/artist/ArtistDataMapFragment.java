package com.groupseven.musicmap.screens.main.artist;

import android.util.Log;

import com.groupseven.musicmap.models.Artist;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.screens.main.map.MapFragment;
import com.groupseven.musicmap.util.firebase.Queries;
import com.groupseven.musicmap.util.map.MusicMemoryOverlay;

import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArtistDataMapFragment extends MapFragment {

    private static final String TAG = "PostMapFragment";

    private final FolderOverlay postsFolder = new FolderOverlay();
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    @Override
    protected void addOverlays() {
        super.addOverlays();

        addOverlay(postsFolder);

        updatePosts();
    }

    @Override
    protected boolean shouldDisplayCurrentLocation() {
        return false;
    }

    @Override
    protected boolean allowInteraction() {
        return false;
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

        User user = Session.getInstance().getCurrentUser();

        if (!user.isArtist() || !((Artist) user).getArtistData().isVerified()) {
            throw new IllegalStateException("ArtistDataMapFragment cannot be used for unverified artist");
        }

        Artist artist = (Artist) user;
        String artistSpotifyId = artist.getArtistData().getSpotifyId();

        // Start fetching music memories
        Queries.getAllMusicMemoriesWithSpotifyArtistId(artistSpotifyId).addOnCompleteListener(completedTask -> {
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
