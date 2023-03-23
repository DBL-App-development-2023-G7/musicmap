package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.musicmap.R;
import com.example.musicmap.feed.FeedAdapter;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.util.firebase.Queries;
import com.example.musicmap.util.ui.Message;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class FeedFragment extends MainFragment {

    private ViewGroup viewGroup;
    private static final String TAG = "FeedFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.viewGroup = container;
        View feedView = inflater.inflate(R.layout.fragment_feed, container, false);

        Activity activity = requireActivity();
        FeedAdapter feedAdapter = new FeedAdapter(activity, R.layout.single_post_layout_feed, new ArrayList<>());
        ListView feedListView = feedView.findViewById(R.id.feed_list);
        feedListView.setAdapter(feedAdapter);

        getFeed(10, new OnFeedDataLoadedListener() {
            @Override
            public void onFeedDataLoaded(List<MusicMemory> feed) {
                feedAdapter.addAll(feed);
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFeedDataLoadFailed() {
                Message.showFailureMessage(viewGroup, "Could not load feed");
            }
        });

        return feedView;
    }

    private void getFeed(int size, OnFeedDataLoadedListener listener) {
        Queries.getAllMusicMemories().addOnCompleteListener(completedTask -> {
            if (completedTask.isSuccessful()) {
                List<MusicMemory> feed = completedTask.getResult().
                        subList(0, Math.min(completedTask.getResult().size(), size));
                listener.onFeedDataLoaded(feed);
            } else {
                Log.e(TAG, completedTask.getException() == null ? "Unclear error" :
                        completedTask.getException().getMessage());
                listener.onFeedDataLoadFailed();
            }
        });
    }

}

interface OnFeedDataLoadedListener {
    void onFeedDataLoaded(List<MusicMemory> feed);
    void onFeedDataLoadFailed();
}