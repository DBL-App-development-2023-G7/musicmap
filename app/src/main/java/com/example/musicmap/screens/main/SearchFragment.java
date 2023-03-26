package com.example.musicmap.screens.main;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
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

public class SearchFragment extends MainFragment {

    public static Track resultTrack;
    // feed
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragmen_search, container, false);


        SpotifyUtils.getGetRecentHistoryRequest().executeAsync().thenApply(
                pageHistory -> {
                    List<CompletableFuture<Track>> trackFutures = Arrays.stream(pageHistory.getItems())
                            .limit(4)
                            .filter(playHistory ->
                                    playHistory.getTrack().getType() == ModelObjectType.TRACK // assumes getTrack is not null
                            ).map(playHistory -> playHistory.getTrack().getId())
                            .map(trackId -> SpotifyUtils.getGetTrackRequest(trackId))
                            .map(request -> request.executeAsync())
                            .collect(Collectors.toList());
                    List<Track>  trackList = new ArrayList<>();

                    for (CompletableFuture<Track> trackFuture: trackFutures) {
                        trackList.add(trackFuture.join());
                    }
                    return  trackList;
                })
                .thenAcceptAsync(
                        trackList -> {
                            SpotifySongAdapter songAdapter = new SpotifySongAdapter(
                                    requireActivity(),
                                    R.layout.single_post_layout_feed,
                                    trackList
                            );
                            ListView songListView = rootView.findViewById(R.id.spotify_search_song_list);
                            songListView.setAdapter(songAdapter);
                        },
                        requireActivity().getMainExecutor()
                );


        // setup search widget
        SearchView searchView = rootView.findViewById(R.id.spotify_search_view);
        searchView.setQueryHint("Search for a song...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                  @Override
                  public boolean onQueryTextSubmit(String query) {
                      // rerender feed
                      Log.d("debug", "[poop] qeury submitted!");
                      SpotifyUtils.getSearchTrackRequest(query).executeAsync()
                              .thenAcceptAsync(trackPaging -> {
                                  List<Track> trackList = Arrays.asList(trackPaging.getItems());
                                  SpotifySongAdapter songAdapter = new SpotifySongAdapter(
                                          requireActivity(),
                                          R.layout.single_post_layout_feed,
                                          trackList
                                  );
                                  ListView songListView = rootView.findViewById(R.id.spotify_search_song_list);
                                  songListView.setAdapter(songAdapter);
                              }, requireActivity().getMainExecutor());
                      return false;
                  }

                  @Override
                  public boolean onQueryTextChange(String query) {
                      return false;
                  }
              }
        );

        return rootView;
    }

}