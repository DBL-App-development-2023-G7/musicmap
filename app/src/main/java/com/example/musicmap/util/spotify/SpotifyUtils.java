package com.example.musicmap.util.spotify;

import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
 * A utilty class to reduce the amount of spotify related code in the main activities
 * However there is a lot of spotify logic happening in activities so this class needs to be expanded
 */
public class SpotifyUtils {

    /**
     * Prepares a request object to get full track data
     * @param trackId the id of the track
     * @return the request object
     */
    public static GetTrackRequest getGetTrackRequest(String trackId) {
        return SpotifyData.getApi().getTrack(trackId).build();
    }

    /**
     * Prepares a request object to retrieve a users listening history
     * @return the request object
     */
    public static GetCurrentUsersRecentlyPlayedTracksRequest getGetRecentHistoryRequest() {
        return SpotifyData.getApi().getCurrentUsersRecentlyPlayedTracks().build();
    }

    /**
     * Prepares a request object to retrieve list of tracks based on a search query
     * @param prompt the serarch prompt
     * @return the request object
     */
    public static SearchTracksRequest getSearchTrackRequest(String prompt) {
        return SpotifyData.getApi().searchTracks(prompt).build();
    }

    /**
     * Loads an image from a given simple song into a view (This uses the SPOTIFY WRAPPER  SIMPLE TRACK CLASS)
     * @param simpleTrack the track contiaing the image
     * @param view the view of the track
     * @param executor the executor on which to laod the image
     */
    public static void loadImageFromSimplifiedTrack(TrackSimplified simpleTrack, ImageView view,  Executor executor) {
        getGetTrackRequest(simpleTrack.getId()).executeAsync()
            .thenAcceptAsync(track -> {
                Log.d("debug", "[poop] Full image data fetched!");
                loadImageFromTrack(track, view);
        },executor);

    }

    /**
     * Loads an image from a given song (This uses the SPOTIFY WRAPPER  SIMPLE TRACK CLASS)
     * @param track the track contiaing the image
     * @param view the view of the track
     */
    public static void loadImageFromTrack(Track track, ImageView view) {
        String imageUrl = track.getAlbum().getImages()[0].getUrl(); // just gets the first image url
        Picasso.get().load(imageUrl).into(view);
    }
}
