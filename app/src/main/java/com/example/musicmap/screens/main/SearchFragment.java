package com.example.musicmap.screens.main;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.musicmap.R;
import com.example.musicmap.feed.FeedAdapter;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.util.spotify.SpotifySongAdapter;
import com.example.musicmap.util.spotify.SpotifyUtils;
import com.google.api.ResourceDescriptor;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
 * The fragment where the user is redirect to search for a track
 */
public class SearchFragment extends MainFragment {

    // temporary store used by post fragment to get search result (I am too lazy to use a Model)
    public static Track resultTrack;
    // this list is computed only once at the start in order to avoid excessive calls to the API
    private List<Track> recentTrackList;
    private View rootView;
    // a countdown timer for the search query to reduce API spam
    private CountDownTimer searchQueryCountdown;
    private  static final  int COUNTDOWN_DELAY = 400;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragmen_search, container, false);
        // I know this is a monstrosity
        // any refactoring / formatting suggestions are welcome
        SpotifyUtils.getGetRecentHistoryRequest().executeAsync().thenAccept(
                pageHistory -> {
                    List<CompletableFuture<Track>> trackFutures = Arrays.stream(pageHistory.getItems())
                            .limit(4) // only get 4 most recent songs (To prevent API calls)
                            .filter(playHistory ->
                                    playHistory.getTrack().getType() == ModelObjectType.TRACK // assumes getTrack is not null
                            ).map(playHistory -> playHistory.getTrack().getId())// actually just get the track id since we only need those for requests
                            .map(trackId -> SpotifyUtils.getGetTrackRequest(trackId)) // prepare request to get full track data (since Album is not in simplified track)
                            .map(request -> request.executeAsync())// call all requests
                            .collect(Collectors.toList());
                    recentTrackList = new ArrayList<>();
                    for (CompletableFuture<Track> trackFuture: trackFutures) {
                        recentTrackList.add(trackFuture.join()); // gather all request results
                    }
                })
                .thenAcceptAsync(
                        empty ->  updateSongListView(recentTrackList),
                        requireActivity().getMainExecutor()
                );


        // setup search widget
        SearchView searchView = rootView.findViewById(R.id.spotify_search_view);
        searchView.setQueryHint("Search for a song...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                  @Override
                  public boolean onQueryTextSubmit(String query) {
                      // rerender feed
                      return false;
                  }
                  @Override
                  public boolean onQueryTextChange(String query) {
                      if(searchQueryCountdown != null) searchQueryCountdown.cancel();
                      searchQueryCountdown = new CountDownTimer(COUNTDOWN_DELAY, 100) {
                          @Override
                          public void onTick(long l) {
                          }

                          @Override
                          public void onFinish() {
                              Log.d("debug", "[poop] qeury submitted!");
                              if(query.equals("")) {
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
        );

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

}