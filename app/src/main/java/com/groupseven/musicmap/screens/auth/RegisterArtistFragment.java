package com.groupseven.musicmap.screens.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.models.ArtistData;
import com.groupseven.musicmap.models.UserData;

public class RegisterArtistFragment extends RegisterFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        if(rootView == null) {
            throw new IllegalStateException("The super class of the RegisterArtistFragment should return a nonnull "
                    + "view.");
        }

        EditText firstNameInput = rootView.findViewById(R.id.username_editText);
        firstNameInput.setHint(R.string.artist_name);
        return rootView;
    }

    @Override
    protected UserData createUserData() {
        return new ArtistData(super.createUserData(), false);
    }

}