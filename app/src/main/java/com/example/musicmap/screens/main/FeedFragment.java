package com.example.musicmap.screens.main;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.musicmap.R;
import com.example.musicmap.feed.FeedAdapter;
import com.example.musicmap.feed.MusicMemory;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.Date;

public class FeedFragment extends MainFragment {

    private ListView feedListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View feedView = inflater.inflate(R.layout.fragment_feed, container, false);
        feedListView = feedView.findViewById(R.id.feed_list);
        MusicMemory[] feed = new MusicMemory[5];

        for (int i = 0; i < 5; i++) {
            Uri uri = Uri.parse("http://upload.wikimedia.org/wikipedia/commons/8/86/Overview_of_Technische_Universiteit_Eindhoven.jpg");
            feed[i] = new MusicMemory(new Date(), new GeoPoint(51.4486, 5.4907), uri);
        }

        System.out.println(Arrays.toString(feed));

        if (getActivity() != null) {
            System.out.println("setting adapter");
            FeedAdapter feedAdapter = new FeedAdapter(getActivity(), R.layout.single_post_layout_feed, feed);
            feedListView.setAdapter(feedAdapter);
        }

        feedListView.setOnItemClickListener((adapterView, view, i, l) -> {

        });

        return feedView;
    }
}