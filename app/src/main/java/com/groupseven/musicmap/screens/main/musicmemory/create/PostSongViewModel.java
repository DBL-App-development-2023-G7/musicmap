package com.groupseven.musicmap.screens.main.musicmemory.create;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.util.spotify.SpotifyUtils;

import org.checkerframework.checker.units.qual.Current;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostSongViewModel extends ViewModel {
    private final MutableLiveData<Bitmap> cameraImage = new MutableLiveData<>(null);
    private final MutableLiveData<Song> selectedSong = new MutableLiveData<>(null);
    private final MutableLiveData<Location> userLocation = new MutableLiveData<>(null);

    public MutableLiveData<Bitmap> getCameraImage() {return cameraImage;}
    public MutableLiveData<Song> getSelectedSong() {return selectedSong;}
    public MutableLiveData<Location> getUserLocation() {return userLocation;}

    public GeoPoint getLocationAsGeoPoint() {
        Location location = userLocation.getValue();
        if (location == null) return null;
        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    public void setSongToCurrentUserSong() {
        if (selectedSong.getValue() == null) {
            SpotifyUtils.getCurrentTrackFuture()
                    .thenAcceptAsync(track -> {
                        if (track != null) {
                            selectedSong.setValue(new Song(track));
                        }
                    });
        }
    }

    public void clearData() {
        this.cameraImage.setValue(null);
        this.selectedSong.setValue(null);
        this.userLocation.setValue(null);
    }

    public boolean isImageNull() {return cameraImage.getValue() == null;}
    public boolean isSongNull() {return selectedSong.getValue() == null;}
    public boolean isLocationNull() {return userLocation.getValue() == null;}


}
