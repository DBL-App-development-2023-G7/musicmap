package com.example.musicmap.util.spotify;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.musicmap.R;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.screens.main.PostFragment;
import com.example.musicmap.screens.main.SearchFragment;
import com.example.musicmap.util.ui.FragmentUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

public class SpotifySongAdapter extends ArrayAdapter<Track> {
    private final Activity activityContext;
    public SpotifySongAdapter(@NonNull Activity activityContext, int resource, @NonNull List<Track> feedItems) {
        super(activityContext, resource, feedItems);
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        View row = convertView;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            row = inflater.inflate(R.layout.single_post_layout_feed, parent, false);
        }

        TextView titleText = row.findViewById(R.id.listview_item_title);
        TextView shortText = row.findViewById(R.id.listview_item_short_description);
        ImageView mainImage = row.findViewById(R.id.listview_image);

        Track spotifyTrack = getItem(position);
        if (spotifyTrack  != null) {
            titleText.setText(spotifyTrack.getName());
            shortText.setText(spotifyTrack.getArtists()[0].toString());
            Picasso.get().load(spotifyTrack.getAlbum().getImages()[0].getUrl()).into(mainImage);
        }

        row.setOnClickListener(view -> goToSearchFragment(spotifyTrack));

        return row;
    }
    private void goToSearchFragment(Track track) {
        FragmentActivity fragmentActivity = (FragmentActivity) activityContext;
        SearchFragment.resultTrack = track;
        FragmentUtil.replaceFragment(fragmentActivity.getSupportFragmentManager(), R.id.fragment_view,
                PostFragment.class);
    }
}