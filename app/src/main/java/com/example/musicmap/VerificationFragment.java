package com.example.musicmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class VerificationFragment extends Fragment {

    private FirebaseAuth auth;
    private AuthActivity activity;

    public VerificationFragment() {
        // Required empty public constructor
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

    private void signOut() {
        activity.loadLoginFragment();
        auth.signOut();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_verification, container, false);

        Button signOutVerificationButton = rootView.findViewById(R.id.signout_verification_button);
        signOutVerificationButton.setOnClickListener(view -> signOut());

        return rootView;
    }
}