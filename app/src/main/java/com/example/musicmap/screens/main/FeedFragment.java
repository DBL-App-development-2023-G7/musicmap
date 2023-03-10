package com.example.musicmap.screens.main;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.musicmap.R;
import com.example.musicmap.feed.FeedAdapter;
import com.example.musicmap.feed.MusicMemory;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedFragment extends MainFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View feedView = inflater.inflate(R.layout.fragment_feed, container, false);
        List<MusicMemory> feed = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String imageURi = "https://www.agconnect.nl/sites/ag/files/2020-05/tu_eindhoven_photo_-_bart_van_overbeeke.jpg.png"; // // CSOFF: LineLength
            Uri uri = Uri.parse(imageURi);
            feed.add(new MusicMemory(new Date(), new GeoPoint(51.4486, 5.4907), uri));
        }

        if (getActivity() != null) {
            FeedAdapter feedAdapter = new FeedAdapter(getActivity(), R.layout.single_post_layout_feed, feed);
            ListView feedListView = feedView.findViewById(R.id.feed_list);
            feedListView.setAdapter(feedAdapter);
        }

        return feedView;
    }
}