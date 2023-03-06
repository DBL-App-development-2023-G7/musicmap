package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

/**
 * This abstract class serves as a base class for fragments in the main package. It extends the Fragment
 * class and provides a reference to the hosting activity.
 */
public abstract class MainFragment extends Fragment {

    /**
     * The hosting activity of the fragment.
     */
    private Activity activity;

    /**
     * Returns the main activity object (host activity)
     * @return main activity object
     */
    public Activity getMainActivity() {
        return activity;
    }

    /**
     * Called to do initial creation of the fragment.
     *
     * @param savedInstanceState if the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onStart() {
        super.onStart();
        this.activity = getActivity();
    }

}
