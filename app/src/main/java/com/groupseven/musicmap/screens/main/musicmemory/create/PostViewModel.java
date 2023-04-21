package com.groupseven.musicmap.screens.main.musicmemory.create;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.groupseven.musicmap.util.spotify.SpotifyUtils;

/**
 * A {@link ViewModel} for data relating to posting a music memory.
 */
public class PostViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> cameraImage = new MutableLiveData<>(null);
    private final MutableLiveData<Song> selectedSong = new MutableLiveData<>(null);
    private final MutableLiveData<Location> userLocation = new MutableLiveData<>(null);

    /**
     * Gets the live data that holds the photo for the post.
     *
     * @return the live data.
     */
    public MutableLiveData<Bitmap> getCameraImage() {
        return cameraImage;
    }

    /**
     * Gets the live data that holds the song for the post.
     *
     * @return the live data.
     */
    public MutableLiveData<Song> getSelectedSong() {
        return selectedSong;
    }

    /**
     * Gets the live data that holds the location for the post.
     *
     * @return the live data.
     */
    public MutableLiveData<Location> getUserLocation() {
        return userLocation;
    }

    /**
     * Gets the {@link #userLocation} as a {@link GeoPoint}.
     *
     * @return the geo point, or {@code null} if the location is {@code null}.
     */
    public GeoPoint getLocationAsGeoPoint() {
        Location location = userLocation.getValue();
        if (location == null) {
            return null;
        }

        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    /**
     * Sets the {@link #selectedSong} to the currently playing song using the Spotify API.
     */
    public void setSongToCurrentUserSong() {
        if (selectedSong.getValue() == null) {
            SpotifyUtils.getCurrentTrackFuture(SpotifyAccess.getSpotifyAccessInstance())
                    .thenAcceptAsync(track -> {
                        if (track != null) {
                            selectedSong.setValue(SpotifyUtils.createSongFromTrack(track));
                        }
                    });
        }
    }

    /**
     * Clears (sets to {@code null}) all data in this view model.
     */
    public void clearData() {
        this.cameraImage.setValue(null);
        this.selectedSong.setValue(null);
        this.userLocation.setValue(null);
    }

    /**
     * Checks if the value of {@link #cameraImage} is {@code null}.
     *
     * @return if it is null.
     */
    public boolean isImageNull() {
        return cameraImage.getValue() == null;
    }

    /**
     * Checks if the value of {@link #selectedSong} is {@code null}.
     *
     * @return if it is null.
     */
    public boolean isSongNull() {
        return selectedSong.getValue() == null;
    }

    /**
     * Checks if the value of {@link #userLocation} is {@code null}.
     *
     * @return if it is null.
     */
    public boolean isLocationNull() {
        return userLocation.getValue() == null;
    }

}
