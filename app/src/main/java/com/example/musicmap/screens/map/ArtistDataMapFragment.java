package com.example.musicmap.screens.map;

import android.util.Log;

import com.example.musicmap.user.Artist;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.Queries;
import com.example.musicmap.util.map.MusicMemoryOverlay;

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
        // TODO can become a large query with many music memories
        //  add util method to use Firestore APIs filtering stuff
        Queries.getAllMusicMemories().addOnCompleteListener(completedTask -> {
            if (completedTask.isSuccessful()) {
                // Add all retrieved music memories to map
                completedTask.getResult().stream()
                        .filter(musicMemory -> artistSpotifyId.equals(musicMemory.getSong().getSpotifyArtistId()))
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
