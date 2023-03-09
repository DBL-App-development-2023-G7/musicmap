package com.example.musicmap.feed;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

        return  row;
    }
}
