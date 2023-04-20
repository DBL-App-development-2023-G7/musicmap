package com.groupseven.musicmap.util.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.screens.main.musicmemory.create.PostFragment;
import com.groupseven.musicmap.util.ui.FragmentUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * The adapter providing data for the spotify song search.
 * <p>
 * Also note that this uses the Spotify WRAPPER Track class not the spotify SDK track class
 */
public class SpotifySongAdapter extends ArrayAdapter<Track> {

    /**
     * The context of the Activity where the adapter is being used.
     */
    private final Activity activityContext;

    /**
     * Constructor for creating the SpotifySongAdapter object.
     *
     * @param activityContext The context of the Activity where the adapter is being used.
     * @param resource The resource ID for the layout file containing the layout for each list item.
     * @param trackItems The list of tracks to be displayed in the list.
     */
    public SpotifySongAdapter(@NonNull Activity activityContext, int resource, @NonNull List<Track> trackItems) {
        super(activityContext, resource, trackItems);
        this.activityContext = activityContext;
    }

    /**
     * This method returns the view for each list item.
     *
     * @param position The position of the list item.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view that this view will eventually be attached to.
     * @return The view for each list item.
     */
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

    /**
     * This method is used to navigate back to the PostFragment when a song is selected.
     *
     * @param track The Track object for the selected list item.
     */
    private void goToPostFragment(Track track) {
        FragmentActivity fragmentActivity = (FragmentActivity) activityContext;
        Bundle searchResult = new Bundle();
        searchResult.putSerializable("song", new Song(track));
        Log.d("SongAdapter", "result!");
        fragmentActivity.getSupportFragmentManager().setFragmentResult(PostFragment.FRAGMENT_RESULT_KEY, searchResult);
        FragmentUtil.replaceFragment(fragmentActivity.getSupportFragmentManager(), R.id.fragment_view,
                PostFragment.class);
    }

}