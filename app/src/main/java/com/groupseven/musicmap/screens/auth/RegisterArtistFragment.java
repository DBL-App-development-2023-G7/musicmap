package com.groupseven.musicmap.screens.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected UserData createUserData() {
        return new ArtistData(super.createUserData(), false);
    }

}