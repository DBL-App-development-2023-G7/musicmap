package com.groupseven.musicmap.screens.main.musicmemory.create;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * The fragment where the user is redirect to search for a track.
 */
public class SearchFragment extends MainFragment {

    private static final int COUNTDOWN_DELAY = 400;

    // temporary store used by post fragment to get search result (I am too lazy to use a Model)

    // this list is computed only once at the start in order to avoid excessive calls to the API
    private final List<Track> recentTrackList = new ArrayList<>();
    private View rootView;

    // a countdown timer for the search query to reduce API spam
    private CountDownTimer searchQueryCountdown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        SpotifyAccess spotifyAccess = SpotifyAccess.getSpotifyAccessInstance();

        // add current track on the top of the track list
        CompletableFuture<Void> currentTrackFuture = SpotifyUtils.getCurrentTrackFuture(spotifyAccess)
                .thenAccept(track -> {
                    if (track != null) {
                        recentTrackList.add(0, track);
                    }
                });

        CompletableFuture<Void> recentTracksFuture = SpotifyUtils.getRecentTracksFuture(4, spotifyAccess)
                .thenAccept(recentTrackList::addAll);

        CompletableFuture.allOf(currentTrackFuture, recentTracksFuture).thenAcceptAsync(unused ->
                        updateSongListView(recentTrackList),
                requireActivity().getMainExecutor());

        // Setup search widget
        SearchView searchView = rootView.findViewById(R.id.spotify_search_view);
        searchView.setQueryHint("Search for a song...");
        searchView.setOnQueryTextListener(new SearchQueryTextListener());

        return rootView;
    }

    private void updateSongListView(List<Track> trackList) {
        Log.d("poop", "updating view!");
        SpotifySongAdapter songAdapter = new SpotifySongAdapter(
                requireActivity(),
                R.layout.single_post_layout_feed,
                trackList
        );
        ListView songListView = rootView.findViewById(R.id.spotify_search_song_list);
        songListView.setAdapter(songAdapter);
    }

    private class SearchQueryTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // re-render feed
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            if (searchQueryCountdown != null) {
                searchQueryCountdown.cancel();
            }

            searchQueryCountdown = new CountDownTimer(COUNTDOWN_DELAY, 100) {
                @Override
                public void onTick(long l) { }

                @Override
                public void onFinish() {
                    if (query.equals("")) {
                        Log.d("poop", "empty q");
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

            searchQueryCountdown.start();
            return false;
        }
    }

}