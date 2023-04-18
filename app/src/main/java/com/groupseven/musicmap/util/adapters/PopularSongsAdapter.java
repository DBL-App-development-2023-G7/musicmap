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

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.models.SongCount;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The adapter providing data for the popular song list.
 */
public class PopularSongsAdapter extends ArrayAdapter<SongCount> {

    /**
     * The context of the Activity where the adapter is being used.
     */
    private final Activity activityContext;

    /**
     * Constructor for creating the PopularSongsAdapter object.
     *
     * @param activityContext The context of the Activity where the adapter is being used.
     * @param resource The resource ID for the layout file containing the layout for each list item.
     * @param songCounts The list of song counts to be displayed in the list.
     */
    public PopularSongsAdapter(@NonNull Activity activityContext, int resource, @NonNull List<SongCount> songCounts) {
        super(activityContext, resource, songCounts);
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
            row = inflater.inflate(R.layout.song_layout_artist_data, parent, false);
        }

        ImageView songImage = row.findViewById(R.id.spotify_album_cover);
        ImageView playSong = row.findViewById(R.id.play_imageView);
        TextView songName = row.findViewById(R.id.song_name);
        TextView streamDetails = row.findViewById(R.id.artist_name);

        SongCount songCount = getItem(position);
        if (songCount != null) {
            Song song = songCount.getSong();
            Long count = songCount.getCount();

            playSong.setVisibility(View.INVISIBLE);
            songName.setText(song.getName());
            songName.setSelected(true);
            streamDetails.setText(String.format(activityContext.getString(R.string.referenced), count));
            Picasso.get().load(song.getImageUri()).into(songImage);
        }

        return row;
    }

}
