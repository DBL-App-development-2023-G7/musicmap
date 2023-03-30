package com.example.musicmap.screens.main;

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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.AbstractRequest;

/**
 * The fragment where the user is redirect to search for a track
 */
public class SearchFragment extends MainFragment {

    private static final int COUNTDOWN_DELAY = 400;

    // temporary store used by post fragment to get search result (I am too lazy to use a Model)
    public static Track resultTrack;

    // this list is computed only once at the start in order to avoid excessive calls to the API
    private List<Track> recentTrackList = new ArrayList<>();
    private View rootView;

    // a countdown timer for the search query to reduce API spam
    private CountDownTimer searchQueryCountdown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragmen_search, container, false);
        // I know this is a monstrosity
        // any refactoring / formatting suggestions are welcome
        SpotifyUtils.getWaitForTokenFuture()
                .thenAccept(unused -> {
                    Log.d("debug", "[poop]3");
                    try {
                        CompletableFuture<CurrentlyPlaying> currentTrackFuture =
                                SpotifyUtils.getCurrentPlayingTrackRequest().executeAsync();
                        CompletableFuture<PagingCursorbased<PlayHistory>> pageHistoryFuture =
                                SpotifyUtils.getGetRecentHistoryRequest().executeAsync();

                        PagingCursorbased<PlayHistory> pageHistory = pageHistoryFuture.join();
                        Log.d("debug", "execute recent history request done");
                        PlayHistory[] historyItems = pageHistory.getItems();
                        List<CompletableFuture<Track>> trackFutures = new ArrayList<>();
                        if (historyItems != null) {
                            trackFutures.addAll(Arrays.stream(historyItems)
                                    .limit(4) // only get 4 most recent songs (To prevent API calls)
                                    .filter(playHistory ->
                                            playHistory.getTrack().getType() == ModelObjectType.TRACK // assumes getTrack is not null
                                    ).map(playHistory -> playHistory.getTrack().getId())// actually just get the track id since we only need those for requests
                                    .map(SpotifyUtils::getGetTrackRequest) // prepare request to get full track data (since Album is not in simplified track)
                                    .map(AbstractRequest::executeAsync)// call all requests
                                    .collect(Collectors.toList()));
                        }

                        CurrentlyPlaying currentTrack = currentTrackFuture.join();
                        if (currentTrack != null) {
                            String currentTrackId = currentTrack.getItem().getId();
                            trackFutures.add(0,
                                    SpotifyUtils.getGetTrackRequest(
                                            currentTrackId
                                    ).executeAsync()
                            );
                        }
                        recentTrackList = new ArrayList<>();
                        for (CompletableFuture<Track> trackFuture : trackFutures) {
                            recentTrackList.add(trackFuture.join()); // gather all request results
                        }
                    } catch (Throwable e) {
                        Log.d("debug", "[poop] Error which we will ignore!");
                        e.printStackTrace();
                    }
                    Log.d("debug", "[poop] End!");
                }).thenAcceptAsync(
                        unused2 -> {
                            try {
                                Log.d("debug", "[poop] 2");
                                updateSongListView(recentTrackList);
                            } catch (Throwable e) {
                                Log.d("debug", "[poop] Error which we will ignore!");
                                e.printStackTrace();
                            }
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
            // rerender feed
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
                    Log.d("debug", "[poop] query submitted!");
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