package com.example.musicmap.screens.auth;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

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
    protected boolean isInputValid(String username, String firstName, String lastName, String email
            , String password, String repeatPassword, Date birthdate) {

        return super.isInputValid(username, firstName, lastName, email, password, repeatPassword,
                birthdate);
    }

}