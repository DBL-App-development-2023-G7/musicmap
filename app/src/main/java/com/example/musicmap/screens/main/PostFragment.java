package com.example.musicmap.screens.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.feed.Song;
import com.example.musicmap.user.Session;
import com.example.musicmap.util.firebase.Actions;
import com.example.musicmap.util.permissions.CameraPermission;
import com.example.musicmap.util.permissions.LocationPermission;
import com.example.musicmap.util.spotify.SpotifyAuthActivity;
import com.example.musicmap.util.ui.FragmentUtil;
import com.example.musicmap.util.ui.ImageUtils;
import com.example.musicmap.util.ui.Message;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostFragment extends MainFragment {

    // needs to be static or data is lost
    // TODO find a better way of persisting data
    private static Bitmap capturedImage;
    private static final String TAG = "PostFragment";

    private final LocationPermission locationPermission = new LocationPermission(this);
    private final CameraPermission cameraPermission = new CameraPermission(this);
    private FusedLocationProviderClient fusedLocationClient;

    private Session currentSession;
    private Activity currentActivity;

    private Location currentLocation;
    private Button addLocationButton;
    private ImageView capturedImagePreview;

    private SpotifyAuthActivity parentActivity;

    // a launcher that launches the camera activity and handles the result
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent resultIntent = result.getData();
                    if (resultIntent == null) {
                        Log.w(TAG, "Activity result from CameraActivity is null");
                        return;
                    }

                    Uri imageUri = resultIntent.getData();

                    try {
                        Picasso.get().load(imageUri)
                                .rotate(ImageUtils.getImageRotationFromEXIF(parentActivity, imageUri))
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        capturedImage = bitmap;
                                        capturedImagePreview.setImageBitmap(capturedImage);
                                        capturedImagePreview.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        Log.d(TAG, "Exception occurred while setting the image", e);
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                                });
                    } catch (IOException e) {
                        Log.d(TAG, "Exception occurred while setting the image", e);
                    }
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currentSession = Session.getInstance();
        this.currentActivity = requireActivity();

        locationPermission.forceRequest();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.currentActivity);
        fetchUserLocation();
//        cameraPermission.request();
        getPermission();
        parentActivity = (SpotifyAuthActivity) this.currentActivity;

        parentActivity.refreshToken(apiToken -> {}, () -> parentActivity.registerForSpotifyPKCE());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        capturedImagePreview = rootView.findViewById(R.id.previewCapturedImage);
        if (capturedImage != null) {
            capturedImagePreview.setImageBitmap(capturedImage);
            capturedImagePreview.setVisibility(View.VISIBLE);
        }

        Button addImageButton = rootView.findViewById(R.id.addImageButton);
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

        addLocationButton = rootView.findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(view -> fetchUserLocation());

        Button postMemoryButton = rootView.findViewById(R.id.postMemoryButton);
        postMemoryButton.setOnClickListener(view -> postMusicMemory());
        return rootView;
    }

    private void goToSearchFragment() {
        FragmentUtil.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.fragment_view,
                SearchFragment.class);
    }

    private void goToCameraActivity() {
        Intent cameraIntent = new Intent(this.currentActivity, CameraActivity.class);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    @SuppressLint("MissingPermission")
    private void fetchUserLocation() {
        if (locationPermission.isCoarseGranted() && locationPermission.isFineGranted()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this.currentActivity, location -> {
                        currentLocation = location;
                        String displayText = getLocationText(currentLocation);
                        addLocationButton.setText(displayText);
                    })
                    .addOnFailureListener(this.currentActivity, exception -> {
                        Log.e(TAG, "Could not fetch user location", exception);
                    });
        } else {
            Message.showFailureMessage(this.currentActivity, "Permission not granted for location");
        }
    }

    // Takes a location and returns a human readable string to display on the search bar
    private String getLocationText(Location location){
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        String resultString = String.format("Lat: %s \nLon: %s",
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );
        Geocoder geocoder = new Geocoder(this.currentActivity);
        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
            if (addressList.size() == 0) {
                Log.w(TAG, "Geocoder could not find an associated address");
                return resultString;
            }

            Address address = addressList.get(0);
            List<String> addressFeatures = new ArrayList<>();

            addressFeatures.add(address.getThoroughfare());
            addressFeatures.add(address.getLocality());
            addressFeatures.add(address.getCountryName());

            resultString = addressFeatures.stream()
                    .filter(Objects::nonNull)
                    .limit(2)
                    .collect(Collectors.joining(", "));

            return resultString;

        } catch (IOException e) {
            Log.w(TAG, "Something went wrong while using Geocoder", e);
            return resultString;
        }
    }

    // TODO Make this monster prettier
    // Below is some questionable code
    private void postMusicMemory() {
        if (SearchFragment.resultTrack == null) {
            Message.showFailureMessage(this.currentActivity, "A track is required to post a music memory!");
            return;
        }

        if (currentLocation == null) {
            Message.showFailureMessage(this.currentActivity, "Location is required to post a music memory!");
            return;
        }

        if (capturedImage == null) {
            Message.showFailureMessage(this.currentActivity, "An image is required to post a music memory!");
            return;
        }

        String authorID = this.currentSession.getCurrentUser().getUid();
        Date timePosted = Calendar.getInstance().getTime();
        GeoPoint geoPointLocation = new GeoPoint(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );

        Song song = new Song(
                SearchFragment.resultTrack.getName(),
                SearchFragment.resultTrack.getArtists()[0].getId(),
                null, // TODO: check this null cause
                SearchFragment.resultTrack.getAlbum().getImages()[0].getUrl(),
                SearchFragment.resultTrack.getPreviewUrl()
        );

        Actions.uploadMusicMemoryImage(capturedImage, authorID).addOnCompleteListener(task -> {
            String imageUrl = task.getResult().toString();
            Actions.postMusicMemory(new MusicMemory(
                    authorID,
                    timePosted,
                    geoPointLocation,
                    imageUrl,
                    song
            )).addOnFailureListener(e ->
                    Message.showSuccessMessage(this.currentActivity, "Successfully created the music memory")
            ).addOnCompleteListener(unused -> {
                        clearData();
                        FragmentUtil.replaceFragment(
                                requireActivity().getSupportFragmentManager(),
                                R.id.fragment_view,
                                FeedFragment.class
                        );
                        Message.showSuccessMessage(this.currentActivity, "Successfully created the music memory");
                    }
            );
        });
    }


    private void clearData() {
        currentLocation = null;
        capturedImage = null;
        SearchFragment.resultTrack = null;
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this.currentActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this.currentActivity,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        }
    }

}