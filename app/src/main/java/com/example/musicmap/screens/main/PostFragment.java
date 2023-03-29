package com.example.musicmap.screens.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.musicmap.R;
import com.example.musicmap.util.spotify.SpotifyAuthActivity;
import com.example.musicmap.util.spotify.SpotifyData;
import com.example.musicmap.util.ui.FragmentUtil;


import com.example.musicmap.util.spotify.SpotifySession;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.net.URI;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

public class PostFragment extends MainFragment {

    private ImageView capturedImagePreview; // Should this be private?

    // a launcher that launches the camera activity and handles the result
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d("debug", "[poop] Camera Activity result recieved!");
                Uri imageUri = result.getData().getData(); // peak code right here
                try {
                    // fetch the result bitmap and display it
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    capturedImagePreview.setImageBitmap(bitmap);
                    capturedImagePreview.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug", "[poop] Fragment create!");
        ((SpotifyAuthActivity) requireActivity()).registerForSpotifyPKCE();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("debug", "[poop] Fragment start!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        getPermission();

        capturedImagePreview = rootView.findViewById(R.id.previewCapturedImage);

        Button addImageButton = rootView.findViewById(R.id.addImageButton); // should this also be defined?
        addImageButton.setOnClickListener(view -> goToCameraActivity());

        Button addSongButton = rootView.findViewById(R.id.addSongButton);
        addSongButton.setOnClickListener(view -> goToSearchFragment());

        ImageView songImageView = rootView.findViewById(R.id.songPreviewImage);

        // load the search result track if there is one
        if (SearchFragment.resultTrack != null) {
            songImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(SearchFragment.resultTrack.getAlbum().getImages()[0].getUrl()).into(songImageView);
            addSongButton.setText(SearchFragment.resultTrack.getName());
        }

        return rootView;
    }


    private void goToSearchFragment() {
        FragmentUtil.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.fragment_view,
                SearchFragment.class);
    }

    private  void goToCameraActivity() {
       Intent cameraIntent = new Intent(requireActivity(), CameraActivity.class);
       cameraActivityResultLauncher.launch(cameraIntent);
    }
    // should be replaced with permissions from util but I don't know how yet and I'm tired
    private void getPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[] {Manifest.permission.CAMERA},
                    100
            );
        }
    }
}