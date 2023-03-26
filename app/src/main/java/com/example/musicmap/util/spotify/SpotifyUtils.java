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

public class SpotifyUtils {

    public static GetTrackRequest getGetTrackRequest(String trackId) {
        return SpotifyData.getApi().getTrack(trackId).build();
    }

    public static GetCurrentUsersRecentlyPlayedTracksRequest getGetRecentHistoryRequest() {
        return SpotifyData.getApi().getCurrentUsersRecentlyPlayedTracks().build();
    }

    public static SearchTracksRequest getSearchTrackRequest(String prompt) {
        return SpotifyData.getApi().searchTracks(prompt).build();
    }

    public static void loadImageFromSimplifiedTrack(TrackSimplified simpleTrack, ImageView view,  Executor executor) {
        getGetTrackRequest(simpleTrack.getId()).executeAsync()
            .thenAcceptAsync(track -> {
                Log.d("debug", "[poop] Full image data fetched!");
                loadImageFromTrack(track, view);
        },executor)
//    optional error handling
//            .whenComplete((response, error) -> {
//                Log.d("debug", "[poop] Complete!");
//                if (error!=null) {
//                    Log.d("debug", String.format("[poop] Error: %s\n%s",error.getMessage(), error.getCause()));
//                }
//        })
        ; // the sacred semicolon

    }

    public static void loadImageFromTrack(Track track, ImageView view) {
        String imageUrl = track.getAlbum().getImages()[0].getUrl(); // just gets the first image url
        Picasso.get().load(imageUrl).into(view);
    }
}
