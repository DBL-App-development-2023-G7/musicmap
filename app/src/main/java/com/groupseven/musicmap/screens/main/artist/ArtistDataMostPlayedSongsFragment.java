package com.groupseven.musicmap.screens.main.artist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.models.Artist;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.screens.main.MainFragment;
import com.groupseven.musicmap.util.adapters.PopularSongsAdapter;
import com.groupseven.musicmap.util.firebase.Queries;
import com.groupseven.musicmap.util.ui.Message;

import java.util.ArrayList;

public class ArtistDataMostPlayedSongsFragment extends MainFragment {

    private static final String TAG = "ArtistDataMostPlayedSongsFragment";
    private static final int NUMBER_OF_SONGS = 10;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mostPlayedSongsView = inflater.inflate(R.layout.fragment_most_played_songs, container, false);
        Activity activity = requireActivity();

        PopularSongsAdapter popularSongsAdapter = new PopularSongsAdapter(activity, R.layout.song_layout_artist_data,
                new ArrayList<>());
        ListView popularSongsListView = mostPlayedSongsView.findViewById(R.id.most_played_list);
        popularSongsListView.setAdapter(popularSongsAdapter);
        User user = Session.getInstance().getCurrentUser();

        if (!user.isArtist() || !((Artist) user).getArtistData().isVerified()) {
            throw new IllegalStateException("ArtistDataMostPopularSongsFragment cannot be served for non-artist user.");
        }

        Artist artist = (Artist) user;
        String spotifyArtistId = artist.getArtistData().getSpotifyId();

        Queries.getMostPopularSongsByArtist(spotifyArtistId, NUMBER_OF_SONGS)
                .whenCompleteAsync((topSongs, throwable) -> {
                    if (throwable == null) {
                        popularSongsAdapter.addAll(topSongs);
                        popularSongsAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Exception occurred while getting most popular songs", throwable);
                        Message.showFailureMessage(requireActivity(), "Could not retrieve most popular songs");
                    }
        }, ContextCompat.getMainExecutor(requireContext()));

        return mostPlayedSongsView;
    }

}
