package com.example.musicmap.screens.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.musicmap.R;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.example.musicmap.util.spotify.SpotifyAppSession;
import com.spotify.protocol.types.Track;


import java.io.IOException;
import java.io.InputStream;


public class PostFragment extends MainFragment {

    private ImageView capturedImagePreview; // Should this be private?
    private ImageView songImageView;
    private Button addSongButton;
    private Button addImageButton;
    // a launcher that launches the camera activity and handles the result
    private SpotifyAppSession spotifyHelper;


    private SpotifyAppRemote mSpotifyAppRemote;
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

    }

    @Override
    public void onStart() {
        super.onStart();
        spotifyHelper = new SpotifyAppSession(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        getPermission();

        capturedImagePreview = rootView.findViewById(R.id.previewCapturedImage);

        addImageButton = rootView.findViewById(R.id.addImageButton); // should this also be defined?
        addImageButton.setOnClickListener(view -> goToCameraFragment());

        addSongButton = rootView.findViewById(R.id.addSongButton);
        addSongButton.setOnClickListener(view -> showCurrentSong());

        songImageView = rootView.findViewById(R.id.songPreviewImage);
        return rootView;
    }

    private void showCurrentSong(){
        spotifyHelper.checkConnection();
        Track lastTrack = spotifyHelper.getLastSong();
        spotifyHelper.debugTrack(lastTrack);
        addSongButton.setText(lastTrack.name);

        songImageView.setVisibility(View.VISIBLE);
        spotifyHelper.loadSongImageIntoView(lastTrack, songImageView);
    }

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

    private  void goToCameraFragment() {
       Intent cameraIntent = new Intent(requireActivity(), CameraActivity.class);
       cameraActivityResultLauncher.launch(cameraIntent);
    }


    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Do whatever you want to do with the bitmap
            songImageView.setVisibility(View.VISIBLE);
            songImageView.setImageBitmap(result);
        }
    }
}