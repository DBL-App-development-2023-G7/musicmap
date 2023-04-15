package com.groupseven.musicmap.util.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.screens.main.musicmemory.create.PostFragment;
import com.groupseven.musicmap.screens.main.musicmemory.create.SearchFragment;
import com.groupseven.musicmap.util.ui.FragmentUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * This adapter has been stolen from the  MusicMemory adapter.
 *
 * Also note that this uses the Spotify WRAPPER Track class not the spotify SDK track class
 */
// TODO change the adapter view
public class SpotifySongAdapter extends ArrayAdapter<Track> {

    private final Activity activityContext;

    public SpotifySongAdapter(@NonNull Activity activityContext, int resource, @NonNull List<Track> feedItems) {
        super(activityContext, resource, feedItems);

        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        View row = convertView;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            row = inflater.inflate(R.layout.spotify_search_song_layout, parent, false);
        }

        TextView titleText = row.findViewById(R.id.search_result_song_title_view);
        TextView shortText = row.findViewById(R.id.search_result_song_artist_view);
        ImageView mainImage = row.findViewById(R.id.search_result_song_image_view);

        Track spotifyTrack = getItem(position);
        if (spotifyTrack != null) {
            titleText.setText(spotifyTrack.getName());
            shortText.setText(spotifyTrack.getArtists()[0].getName());
            Picasso.get().load(spotifyTrack.getAlbum().getImages()[0].getUrl()).into(mainImage);
        }

        row.setOnClickListener(view -> goToPostFragment(spotifyTrack));

        return row;
    }

    // a return back to the post and set the search result track
    private void goToPostFragment(Track track) {
        FragmentActivity fragmentActivity = (FragmentActivity) activityContext;
        SearchFragment.setResultTrack(track);

        FragmentUtil.replaceFragment(fragmentActivity.getSupportFragmentManager(), R.id.fragment_view,
                PostFragment.class);
    }

}