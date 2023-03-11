package com.example.musicmap.feed;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.musicmap.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedAdapter extends ArrayAdapter<MusicMemory> {

    private final Activity activityContext;

    public FeedAdapter(@NonNull Activity activityContext, int resource, @NonNull List<MusicMemory> feedItems) {
        super(activityContext, resource, feedItems);
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            row = inflater.inflate(R.layout.single_post_layout_feed, parent, false);
        }

        TextView titleText = row.findViewById(R.id.listview_item_title);
        TextView shortText = row.findViewById(R.id.listview_item_short_description);
        ImageView mainImage = row.findViewById(R.id.listview_image);

        MusicMemory musicMemory = getItem(position);
        if (musicMemory != null) {
            titleText.setText(musicMemory.getTimePosted().toString());
            shortText.setText(musicMemory.getLocation().toString());
            Picasso.get().load(musicMemory.getPhoto()).into(mainImage);
        }

        return row;
    }

}
