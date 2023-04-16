package com.groupseven.musicmap.util.spotify;

import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.AbstractRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
 * A utility class to reduce the amount of spotify related code in the main activities.
 * <p>
 * However there is a lot of spotify logic happening in activities so this class needs to be expanded.
 */
// TODO REPLACE FUTURES WITH CLASSES
public class SpotifyUtils {

    public static final String TAG = "SpotifyUtils";

    public static CompletableFuture<List<Track>> getRecentTracksFuture(int maxTracks) {
        return getWaitForTokenFuture().thenApply(unused -> {
            List<Track> recentTrackList = new ArrayList<>();
            try {
                PagingCursorbased<PlayHistory> pageHistory =
                        SpotifyUtils.getGetRecentHistoryRequest().execute();
                PlayHistory[] historyItems = pageHistory.getItems();
                List<CompletableFuture<Track>> trackFutures = new ArrayList<>();

                if (historyItems != null) {
                    trackFutures.addAll(Arrays.stream(historyItems)
                            .limit(maxTracks) // only get 4 most recent songs (To prevent API calls)
                            .filter(playHistory ->
                                            playHistory.getTrack().getType() == ModelObjectType.TRACK
                                    // assumes getTrack is not null
                            ).map(playHistory -> playHistory.getTrack().getId())
                            // actually just get the track id since we only need those for requests
                            .map(SpotifyUtils::getGetTrackRequest)
                            // prepare request to get full track data (since Album is not in simplified track)
                            .map(AbstractRequest::executeAsync)// call all requests
                            .collect(Collectors.toList()));
                }

                for (CompletableFuture<Track> trackFuture : trackFutures) {
                    recentTrackList.add(trackFuture.join()); // gather all request results
                }
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                Log.d(TAG, "Spotify web api exception!", e);
            }
            return recentTrackList;
        });
    }

    /**
     * Waits for spotify token and then returns the current track.
     * @return The current spotify track as a completaBLE FURTU
     */
    public static CompletableFuture<Track> getCurrentTrackFuture() {
        return getWaitForTokenFuture().thenApply(unused -> {
            Track currentTrack = null;
            try {
                CurrentlyPlaying currentSimpleTrack =
                        SpotifyUtils.getCurrentPlayingTrackRequest().execute();
                if (currentSimpleTrack != null) {
                    String currentTrackId = currentSimpleTrack.getItem().getId();
                    currentTrack = SpotifyUtils.getGetTrackRequest(currentTrackId).execute();
                }
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                Log.d(TAG, "Spotify web api exception!", e);
            }
            return currentTrack;
        });
    }

    public static CompletableFuture<Void> getWaitForTokenFuture() {
        return CompletableFuture.runAsync(() -> {
            while (SpotifyData.getApi() == null) {
                try {
                    Log.d(TAG, "Waiting for token.");

                    //TODO look for better options
                    Thread.sleep(50); // I DO NOT CARE ABOUT THE WARNING!!!!
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Log.d(TAG, "Token found!");
        });
    }

    /**
     * Prepares a request object to get full track data.
     *
     * @param trackId the id of the track
     * @return the request object
     */
    public static GetTrackRequest getGetTrackRequest(String trackId) {
        SpotifyApi api = SpotifyData.getApi();

        if (api == null) {
            throw new NullPointerException("No spotify API loaded");
        }

        return SpotifyData.getApi().getTrack(trackId).build();
    }

    /**
     * Prepares a request object to retrieve a users listening history.
     *
     * @return the request object
     */
    public static GetCurrentUsersRecentlyPlayedTracksRequest getGetRecentHistoryRequest() {
        return SpotifyData.getApi().getCurrentUsersRecentlyPlayedTracks().build();
    }

    /**
     * Prepares a request object to retrieve list of tracks based on a search query.
     *
     * @param prompt the search prompt
     * @return the request object
     */
    public static CompletableFuture<Paging<Track>> getSearchTrackFuture(String prompt) {
        return SpotifyData.getApi().searchTracks(prompt).build().executeAsync();
    }

    /**
     * Loads an image from a given simple song into a view (This uses the SPOTIFY WRAPPER SIMPLE TRACK CLASS).
     *
     * @param simpleTrack the track containing the image
     * @param view        the view of the track
     * @param executor    the executor on which to load the image
     */
    public static void loadImageFromSimplifiedTrack(TrackSimplified simpleTrack, ImageView view, Executor executor) {
        getGetTrackRequest(simpleTrack.getId()).executeAsync()
                .thenAcceptAsync(track -> {
                    Log.d(TAG, "Full image data fetched!");
                    loadImageFromTrack(track, view);
                }, executor);
    }

    /**
     * Loads an image from a given song (This uses the SPOTIFY WRAPPER  SIMPLE TRACK CLASS).
     *
     * @param track the track containing the image
     * @param view  the view of the track
     */
    public static void loadImageFromTrack(Track track, ImageView view) {
        String imageUrl = track.getAlbum().getImages()[0].getUrl(); // just gets the first image url
        Picasso.get().load(imageUrl).into(view);
    }

    public static GetUsersCurrentlyPlayingTrackRequest getCurrentPlayingTrackRequest() {
        return SpotifyData.getApi().getUsersCurrentlyPlayingTrack().additionalTypes("track").build();
    }

}
