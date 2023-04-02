package com.example.musicmap.screens.main;

import static com.example.musicmap.util.spotify.SpotifyUtils.getCurrentTrackFuture;
import static com.example.musicmap.util.spotify.SpotifyUtils.getRecentTracksFuture;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.musicmap.R;
import com.example.musicmap.util.spotify.SpotifySongAdapter;
import com.example.musicmap.util.spotify.SpotifyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * The fragment where the user is redirect to search for a track
 */
public class SearchFragment extends MainFragment {

    private static final int COUNTDOWN_DELAY = 400;

    // temporary store used by post fragment to get search result (I am too lazy to use a Model)
    public static Track resultTrack;

    // this list is computed only once at the start in order to avoid excessive calls to the API
    private final List<Track> recentTrackList = new ArrayList<>();
    private View rootView;

    // a countdown timer for the search query to reduce API spam
    private CountDownTimer searchQueryCountdown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragmen_search, container, false);

        // If a spotify token is present
        // get recent tracks and add them to the view.
        SpotifyUtils.getWaitForTokenFuture()
                .thenCompose(unused ->
                        getRecentTracksFuture(2).thenAcceptBoth(
                                getCurrentTrackFuture(),
                                (trackList, currentTrack) -> {
                                    Log.d("debug", String.format("track size: %d", trackList.size()));
                                    if(currentTrack != null){
                                        recentTrackList.add(currentTrack);
                                    }
                                    recentTrackList.addAll(trackList);
                                    Log.d("debug", String.format("track size2: %d", recentTrackList.size()));
                                }
                        )
                ).thenAcceptAsync(
                        unused -> {
                            Log.d("debug", "Draw track");
                            updateSongListView(recentTrackList);
                        },
                        requireActivity().getMainExecutor()
                );

        // setup search widget
        SearchView searchView = rootView.findViewById(R.id.spotify_search_view);
        searchView.setQueryHint("Search for a song...");

        searchView.setOnQueryTextListener(new SearchQueryTextListener());

        return rootView;
    }

    // TODO find a more efficient method of rendering results
    // currently we just destroy and rebuild the view
    private void updateSongListView(List<Track> trackList) {
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
                        updateSongListView(recentTrackList);
                    } else {
                        SpotifyUtils.getSearchTrackRequest(query).executeAsync()
                                .thenAcceptAsync(trackPaging -> {
                                    List<Track> trackList = Arrays.asList(trackPaging.getItems());
                                    updateSongListView(trackList);
                                }, requireActivity().getMainExecutor());
                    }
                }
            };

            searchQueryCountdown.start();
            return false;
        }
    }

}