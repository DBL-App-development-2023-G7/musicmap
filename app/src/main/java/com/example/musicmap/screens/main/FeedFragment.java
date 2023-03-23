package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.musicmap.R;
import com.example.musicmap.feed.FeedAdapter;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.util.firebase.Queries;
import com.example.musicmap.util.ui.Message;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends MainFragment {

    private ViewGroup viewGroup;
    private static final String TAG = "FeedFragment";
    private int singleFetchCount;
    private int fetchCount;
    private int feedSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.viewGroup = container;
        this.singleFetchCount = 4;
        this.fetchCount = 1;
        this.feedSize = 100;

        View feedView = inflater.inflate(R.layout.fragment_feed, container, false);

        Activity activity = requireActivity();
        FeedAdapter feedAdapter = new FeedAdapter(activity, R.layout.single_post_layout_feed, new ArrayList<>());
        ListView feedListView = feedView.findViewById(R.id.feed_list);
        feedListView.setAdapter(feedAdapter);

        feedListView.setOnScrollListener(onScrollListener(feedAdapter));

        getFeed(singleFetchCount, new OnFeedDataLoadedListener() {
            @Override
            public void onFeedDataLoaded(List<MusicMemory> feed) {
                feedAdapter.addAll(feed);
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFeedDataLoadFailed() {
                Message.showFailureMessage(viewGroup, getString(R.string.feed_error_loading));
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
                Log.e(TAG, completedTask.getException() == null ? "Unclear error"
                        : completedTask.getException().getMessage());
                listener.onFeedDataLoadFailed();
            }
        });
    }

    private AbsListView.OnScrollListener onScrollListener(FeedAdapter feedAdapter) {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount == 0 || feedAdapter.getCount() == feedSize || fetchCount > feedSize) {
                    return;
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    getFeed(feedAdapter.getCount() + singleFetchCount, new OnFeedDataLoadedListener() {
                        @Override
                        public void onFeedDataLoaded(List<MusicMemory> feed) {
                            // get feed from the last fetched music memory
                            // TODO: check if we can move this to the firebase query somehow
                            feed = feed.subList(feedAdapter.getCount(), feed.size());
                            if (feed.size() == 0) {
                                return;
                            }

                            feedAdapter.addAll(feed);
                            feedAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFeedDataLoadFailed() {}
                    });
                }
            }
        };
    }

}

/**
 * Interface to listen to feed fetching task and set feed or exception accordingly.
 */
interface OnFeedDataLoadedListener {
    void onFeedDataLoaded(List<MusicMemory> feed);

    void onFeedDataLoadFailed();
}