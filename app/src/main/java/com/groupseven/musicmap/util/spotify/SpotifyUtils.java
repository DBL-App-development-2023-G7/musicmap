package com.groupseven.musicmap.util.spotify;

import android.util.Log;

import com.groupseven.musicmap.spotify.SpotifyAccess;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.AbstractRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
 * A utility class for Spotify API.
 */
public class SpotifyUtils {

    public static final String TAG = "SpotifyUtils";

    /**
     * Returns a CompletableFuture that retrieves a list of the user's most recent tracks
     * from the Spotify API.
     *
     * @param maxTracks The maximum number of tracks to retrieve.
     * @param spotifyAccess The user's access token and the Spotify Web API object.
     * @return A CompletableFuture that, when completed, returns a List of Track objects
     * representing the user's most recent tracks.
     */
    public static CompletableFuture<List<Track>> getRecentTracksFuture(int maxTracks, SpotifyAccess spotifyAccess) {
        return checkForSpotifyToken(spotifyAccess).thenApply(unused -> {
            List<Track> recentTrackList = new ArrayList<>();
            try {
                PagingCursorbased<PlayHistory> pageHistory = SpotifyUtils
                        .getGetRecentHistoryRequest(spotifyAccess).execute();
                PlayHistory[] historyItems = pageHistory.getItems();
                List<CompletableFuture<Track>> trackFutures = new ArrayList<>();

                if (historyItems != null) {
                    trackFutures.addAll(Arrays.stream(historyItems)
                            .limit(maxTracks)
                            .filter(playHistory -> playHistory.getTrack().getType() == ModelObjectType.TRACK)
                            .map(playHistory -> playHistory.getTrack().getId())
                            .map(trackId -> SpotifyUtils.getGetTrackRequest(spotifyAccess, trackId))
                            .map(AbstractRequest::executeAsync)
                            .collect(Collectors.toList()));
                }

                for (CompletableFuture<Track> trackFuture : trackFutures) {
                    recentTrackList.add(trackFuture.join());
                }
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                Log.d(TAG, "Spotify web api exception!", e);
            }
            return recentTrackList;
        });
    }

    /**
     * Returns a CompletableFuture that retrieves the user's currently playing track
     * from the Spotify Web API.
     *
     * @param spotifyAccess The user's access token and the Spotify Web API object.
     * @return A CompletableFuture that, when completed, returns a Track object representing
     * the user's currently playing track.
     */
    public static CompletableFuture<Track> getCurrentTrackFuture(SpotifyAccess spotifyAccess) {
        return checkForSpotifyToken(spotifyAccess).thenApply(unused -> {
            Track currentTrack = null;
            try {
                CurrentlyPlaying currentSimpleTrack = getCurrentPlayingTrackRequest(spotifyAccess).execute();
                if (currentSimpleTrack != null) {
                    String currentTrackId = currentSimpleTrack.getItem().getId();
                    currentTrack = SpotifyUtils.getGetTrackRequest(spotifyAccess, currentTrackId).execute();
                }
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                Log.d(TAG, "Spotify web api exception!", e);
            }
            return currentTrack;
        });
    }

    /**
     * Returns a CompletableFuture that waits for the SpotifyAccess object to contain a
     * non-null SpotifyDataApi object (i.e., a valid access token).
     *
     * @param spotifyAccess The user's access token and the Spotify Web API object.
     * @return A CompletableFuture that completes when the access token is retrieved.
     */
    public static CompletableFuture<Void> checkForSpotifyToken(SpotifyAccess spotifyAccess) {
        int pollIntervalMillis = 50;
        int timeoutSeconds = 30;
        int numPolls = timeoutSeconds * 1000 / pollIntervalMillis;

        CountDownLatch latch = new CountDownLatch(1);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (spotifyAccess.getSpotifyDataApi() != null) {
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

    /**
     * Generates a random code verifier for use in the Spotify API authentication flow.
     *
     * @return A string containing the randomly generated code verifier.
     */
    public static String generateCodeVerifier() {
        final int VERIFIER_LEN = 50;
        byte[] codeVerifier = new byte[VERIFIER_LEN];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(codeVerifier);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    /**
     * Generates a code challenge for use in the Spotify API authentication flow,
     * based on the provided code verifier.
     *
     * @param codeVerifier The code verifier to generate the code challenge from.
     * @return A string containing the code challenge.
     */
    public static String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();

            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e); // SHA-256 support is required for every Java implementation
        }
    }

    /**
     * Prepares a request object to get full track data.
     *
     * @param spotifyAccess the spotifyAccess instance
     * @param trackId the id of the track
     * @return the request object
     */
    public static GetTrackRequest getGetTrackRequest(SpotifyAccess spotifyAccess, String trackId) {
        return spotifyAccess.getSpotifyDataApi().getTrack(trackId).build();
    }

    /**
     * Prepares a request object to retrieve a users listening history.
     *
     * @param spotifyAccess the spotifyAccess instance
     * @return the request object
     */
    public static GetCurrentUsersRecentlyPlayedTracksRequest getGetRecentHistoryRequest(SpotifyAccess spotifyAccess) {
        return spotifyAccess.getSpotifyDataApi().getCurrentUsersRecentlyPlayedTracks().build();
    }

    /**
     * Prepares a request object to retrieve list of tracks based on a search query.
     *
    * @param spotifyAccess the spotifyAccess instance
     * @param prompt the search prompt
     * @return the request object
     */
    public static SearchTracksRequest getSearchTrackRequest(SpotifyAccess spotifyAccess, String prompt) {
        return spotifyAccess.getSpotifyDataApi().searchTracks(prompt).build();
    }

    /**
     * Gets the Current playing track request to get the current playing track from Spotify.
     *
     * @param spotifyAccess the spotifyAccess instance
     * @return the request object
     */
    public static GetUsersCurrentlyPlayingTrackRequest getCurrentPlayingTrackRequest(SpotifyAccess spotifyAccess) {
        return spotifyAccess.getSpotifyDataApi()
                .getUsersCurrentlyPlayingTrack()
                .additionalTypes("track")
                .build();
    }

}
