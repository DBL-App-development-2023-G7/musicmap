package com.groupseven.musicmap.util.spotify;

import android.util.Log;
import android.widget.ImageView;

import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.squareup.picasso.Picasso;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.AbstractRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
 * A utility class to reduce the amount of spotify related code in the main activities.
 * <p>
 * However there is a lot of spotify logic happening in activities so this class needs to be expanded.
 */
public class SpotifyUtils {

    public static final String TAG = "SpotifyUtils";

    public static CompletableFuture<List<Track>> getRecentTracksFuture(int maxTracks) {
        return CompletableFuture.supplyAsync(() -> {
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

    public static CompletableFuture<Track> getCurrentTrackFuture() {
        return CompletableFuture.supplyAsync(() -> {
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

    public static CompletableFuture<Void> checkForSpotifyToken() {
        int pollIntervalMillis = 50;
        int timeoutSeconds = 30;
        int numPolls = timeoutSeconds * 1000 / pollIntervalMillis;

        CountDownLatch latch = new CountDownLatch(1);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (SpotifyAccess.getApi() != null) {
                executor.shutdown();
                latch.countDown();
                Log.d(TAG, "Token found!");
            } else {
                Log.d(TAG, "Waiting for token.");
            }
        }, 0, pollIntervalMillis, TimeUnit.MILLISECONDS);

        return CompletableFuture.runAsync(() -> {
            try {
                if (!latch.await(numPolls * pollIntervalMillis, TimeUnit.MILLISECONDS)) {
                    Log.w(TAG, "Timeout waiting for token.");
                    executor.shutdown();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                executor.shutdown();
                Log.w(TAG, "Thread interrupted while waiting for token.");
            }
        });
    }

    public static String getSpotifyPermissions() {
        return "user-read-currently-playing,user-read-recently-played";
    }

    public static String generateCodeVerifier() {
        final int VERIFIER_LEN = 50;
        byte[] codeVerifier = new byte[VERIFIER_LEN];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(codeVerifier);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    public static String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();

            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares a request object to get full track data.
     *
     * @param trackId the id of the track
     * @return the request object
     */
    public static GetTrackRequest getGetTrackRequest(String trackId) {
        SpotifyApi api = SpotifyAccess.getApi();

        if (api == null) {
            throw new NullPointerException("No spotify API loaded");
        }

        return SpotifyAccess.getApi().getTrack(trackId).build();
    }

    /**
     * Prepares a request object to retrieve a users listening history.
     *
     * @return the request object
     */
    public static GetCurrentUsersRecentlyPlayedTracksRequest getGetRecentHistoryRequest() {
        return SpotifyAccess.getApi().getCurrentUsersRecentlyPlayedTracks().build();
    }

    /**
     * Prepares a request object to retrieve list of tracks based on a search query.
     *
     * @param prompt the search prompt
     * @return the request object
     */
    public static SearchTracksRequest getSearchTrackRequest(String prompt) {
        return SpotifyAccess.getApi().searchTracks(prompt).build();
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
        return SpotifyAccess.getApi().getUsersCurrentlyPlayingTrack().additionalTypes("track").build();
    }

}
