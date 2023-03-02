package com.example.musicmap.screens.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.musicmap.R;

public class VerificationFragment extends AuthFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_verification, container, false);

        Button signOutVerificationButton = rootView.findViewById(R.id.signout_verification_button);
        signOutVerificationButton.setOnClickListener(view -> signOut());

        return rootView;
    }

    private void signOut() {
        activity.loadLoginFragment();
        auth.signOut();
    }

}