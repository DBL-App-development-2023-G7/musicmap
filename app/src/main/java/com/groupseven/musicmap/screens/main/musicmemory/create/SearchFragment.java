package com.groupseven.musicmap.screens.main.musicmemory.create;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.screens.main.MainFragment;
import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.groupseven.musicmap.util.adapters.SpotifySongAdapter;
import com.groupseven.musicmap.util.spotify.SpotifyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * The fragment where the user is redirect to search for a track.
 */
public class SearchFragment extends MainFragment {

    /**
     * Stores the tracks the user has recently listened to.
     * <p>
     * In a list to avoid excessive Spotify API calls.
     */
    private final List<Track> recentTrackList = Collections.synchronizedList(new ArrayList<>());

    /**
     * The {@link ListView} displaying the feed of songs the user can choose from.
     */
    private ListView songListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        songListView = rootView.findViewById(R.id.spotify_search_song_list);

        SpotifyAccess spotifyAccess = SpotifyAccess.getSpotifyAccessInstance();

        // The song that is currently playing
        CompletableFuture<Void> currentTrackFuture = SpotifyUtils.getCurrentTrackFuture(spotifyAccess)
                .thenAccept(track -> {
                    if (track != null) {
                        recentTrackList.add(0, track);
                    }
                });
        // The songs recently listened to
        CompletableFuture<Void> recentTracksFuture = SpotifyUtils.getRecentTracksFuture(4, spotifyAccess)
                .thenAccept(recentTrackList::addAll);

        // When both requests are done, update the song feed
        CompletableFuture.allOf(currentTrackFuture, recentTracksFuture).thenAcceptAsync(unused ->
                        updateSongListView(recentTrackList),
                requireActivity().getMainExecutor());

        // Setup search widget
        SearchView searchView = rootView.findViewById(R.id.spotify_search_view);
        searchView.setQueryHint(getString(R.string.song_search_hint));
        searchView.setOnQueryTextListener(new SearchQueryTextListener());

        return rootView;
    }

    /**
     * Displays the given list of tracks in the feed.
     *
     * @param trackList the tracks.
     */
    private void updateSongListView(List<Track> trackList) {
        SpotifySongAdapter songAdapter = new SpotifySongAdapter(
                requireActivity(),
                R.layout.single_post_layout_feed,
                trackList
        );
        songListView.setAdapter(songAdapter);
    }

    /**
     * Listens to changes in the query text field.
     */
    private class SearchQueryTextListener implements SearchView.OnQueryTextListener {

        /**
         * The amount of milliseconds between Spotify API requests while entering a query.
         */
        private static final int COUNTDOWN_DELAY = 400;

        /**
         * A countdown for API requests to reduce the amount of Spotify API requests.
         */
        private CountDownTimer queryCountdown;

        @Override
        public boolean onQueryTextSubmit(String query) {
            // Re-renders feed
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            // Cancel the request from the previous query change if applicable
            if (queryCountdown != null) {
                queryCountdown.cancel();
            }

            // A countdown system to prevent sending API requests for every character typed,
            //  only send a request when the user hasn't typed for QUERY_INTERVAL ms
            queryCountdown = new CountDownTimer(COUNTDOWN_DELAY, COUNTDOWN_DELAY) {
                @Override
                public void onTick(long l) { }

                @Override
                public void onFinish() {
                    if (query.isEmpty()) {
                        updateSongListView(recentTrackList);
                    } else {
                        SpotifyUtils.getSearchTrackRequest(SpotifyAccess.getSpotifyAccessInstance(), query)
                                .executeAsync().thenAcceptAsync(trackPaging -> {
                                    List<Track> searchedTrackList = Arrays.asList(trackPaging.getItems());
                                    updateSongListView(searchedTrackList);
                                }, requireActivity().getMainExecutor());
                    }
                }
            };

            queryCountdown.start();
            return false;
        }
    }

}