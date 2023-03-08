package com.example.musicmap.screens.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.musicmap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeedFragment extends MainFragment {

    private ListView feedListView;
    private int number = 40, firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        feedListView = view.findViewById(R.id.feed_list);
        List<Map<String, String>> feedList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("listview_item_title", "Title " + i);
            map.put("listview_image", "https://static.toiimg.com/photo/msid-58515713,width-96,height-65.cms");
            map.put("listview_item_short_description", "Subtitle " + i);
            feedList.add(map);
        }

        System.out.println(feedList);

        SimpleAdapter.ViewBinder viewBinder = (view1, data, textRep) -> {
            System.out.println(view1.getId() + " -> ");
            if (view1.getId() == R.id.listview_item_title) {
                ((TextView) view1).setText((String) data);
                return true;
            } else if (view1.getId() == R.id.listview_image) {
                if (data != null) {
                    Glide.with(FeedFragment.this.requireContext()).load(data).centerCrop().into((ImageView) view1);
                }
                return true;
            } else if (view1.getId() == R.id.listview_item_short_description) {
                ((TextView) view1).setText((String) data);
                return true;
            }

            return false;
        };

        String[] from = {"listview_image", "listview_item_title", "listview_item_short_description"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(FeedFragment.this.getContext(), feedList,
                R.layout.single_post_layout_feed, from, to);
        simpleAdapter.setViewBinder(viewBinder);
        feedListView.setAdapter(simpleAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }
}