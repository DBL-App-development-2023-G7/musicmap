package com.example.musicmap.screens.artist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicmap.R;
import com.example.musicmap.feed.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PopularSongsAdapter extends ArrayAdapter<SongCount> {

    private final Activity activityContext;

    public PopularSongsAdapter(@NonNull Activity activityContext, int resource, @NonNull List<SongCount> songs) {
        super(activityContext, resource, songs);
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            row = inflater.inflate(R.layout.song_layout_artist_data, parent, false);
        }

        ImageView songImage = row.findViewById(R.id.popular_song_image);
        TextView songName = row.findViewById(R.id.popular_song_name);
        TextView streamDetails = row.findViewById(R.id.popular_song_count);

        SongCount songCount = getItem(position);
        if (songCount != null) {
            Song song = songCount.getSong();
            Long count = songCount.getCount();

            songName.setText(song.getName());
            streamDetails.setText(String.format(activityContext.getString(R.string.referenced), count));
            Picasso.get().load(song.getImageUri()).into(songImage);
        }

        return row;
    }

}
