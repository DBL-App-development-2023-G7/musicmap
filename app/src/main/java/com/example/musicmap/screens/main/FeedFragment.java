package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public abstract class FeedFragment extends Fragment {
    protected Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.activity = getActivity();
    }
}
