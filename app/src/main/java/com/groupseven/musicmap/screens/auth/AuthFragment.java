package com.groupseven.musicmap.screens.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public abstract class AuthFragment extends Fragment {

    private FirebaseAuth auth;

    private AuthActivity activity;

    public FirebaseAuth getAuth() {
        return auth;
    }

    public AuthActivity getAuthActivity() {
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = (AuthActivity) getActivity();
    }

}