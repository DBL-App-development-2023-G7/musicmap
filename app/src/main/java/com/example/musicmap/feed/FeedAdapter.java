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

public class FeedAdapter extends ArrayAdapter {
    private MusicMemory[] feedItems;
    private Activity activityContext;

    public FeedAdapter(@NonNull Activity activityContext, int resource, MusicMemory[] feedItems) {
        super(activityContext, resource);
        this.activityContext = activityContext;
        this.feedItems = feedItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = this.activityContext.getLayoutInflater();
        if (convertView == null) {
            row = inflater.inflate(R.layout.single_post_layout_feed, null, true);
        }

        TextView titleText = row.findViewById(R.id.listview_item_title);
        TextView shortText = row.findViewById(R.id.listview_item_short_description);
        ImageView mainImage = row.findViewById(R.id.listview_image);

        titleText.setText(feedItems[position].getTimePosted().toString());
        shortText.setText(feedItems[position].getLocation().toString());
        mainImage.setImageURI(feedItems[position].getPhoto());
        return  row;
    }
}
