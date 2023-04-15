package com.groupseven.musicmap.screens.main.musicmemory.create;

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

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.screens.main.MainFragment;
import com.groupseven.musicmap.screens.main.feed.FeedFragment;
import com.groupseven.musicmap.util.firebase.Actions;
import com.groupseven.musicmap.util.permissions.CameraPermission;
import com.groupseven.musicmap.util.permissions.LocationPermission;
import com.groupseven.musicmap.util.spotify.SpotifyAuthActivity;
import com.groupseven.musicmap.util.spotify.SpotifyUtils;
import com.groupseven.musicmap.util.ui.FragmentUtil;
import com.groupseven.musicmap.util.ui.ImageUtils;
import com.groupseven.musicmap.util.ui.Message;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
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

import se.michaelthelin.spotify.model_objects.specification.Track;

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

    private ImageView songImageView;
    private Button addSongButton;

    private Location currentLocation;
    private Button addLocationButton;
    private ImageView capturedImagePreview;
    private Button postMemoryButton;
    private boolean shouldClearData = true;
    private SpotifyAuthActivity parentActivity;

    // a launcher that launches the camera activity and handles the result
    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
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

                    Log.d(TAG, "Camera Activity result is called, URI: " + imageUri);

                    try {
                        Picasso.get().load(imageUri)
                                .rotate(ImageUtils.getImageRotationFromEXIF(parentActivity, imageUri))
                                .into(cameraImageTarget);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception occurred while setting the image", e);
                    }
                }
            }
    );

    // this is a Picasso target into which Picasso will load the image taken from the camera
    // in a field so it won't be garbage collected
    private final Target cameraImageTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d(TAG, "Camera Image Bitmap Loaded");
            capturedImage = bitmap;
            capturedImagePreview.setImageBitmap(capturedImage);
            capturedImagePreview.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Log.e(TAG, "Exception occurred while setting the image", e);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d(TAG, "Prepare Camera Image Load");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currentSession = Session.getInstance();
        this.currentActivity = requireActivity();

        locationPermission.forceRequest();

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(currentActivity)
                == ConnectionResult.SUCCESS) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.currentActivity);
        } else {
            int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(currentActivity);
            Log.e(TAG, "Google Play services availability response: " + response);
        }

        fetchUserLocation();
        getPermission();

        parentActivity = (SpotifyAuthActivity) this.currentActivity;
        parentActivity.refreshToken(apiToken -> {
            postMemoryButton.setEnabled(true);
        }, () -> {
            Message.showFailureMessage(this.currentActivity, getString(R.string.error_spotify_not_connected));
            postMemoryButton.setEnabled(false);
        });
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

        addSongButton = rootView.findViewById(R.id.addSongButton);
        addSongButton.setOnClickListener(view -> goToSearchFragment());

        songImageView = rootView.findViewById(R.id.songPreviewImage);

        // get current song if no song has been searched for
        if (SearchFragment.getResultTrack() == null) {
            SpotifyUtils.getWaitForTokenFuture().thenApply(
                    unused -> SpotifyUtils.getCurrentTrackFuture().join()
            ).thenAcceptAsync(
                    track -> {
                        if (track != null) {
                            SearchFragment.setResultTrack(track);
                            setSelectedTrack(track);
                        }
                    },
                    parentActivity.getMainExecutor()
            );
        } else {
            setSelectedTrack(SearchFragment.getResultTrack());
        }

        addLocationButton = rootView.findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(view -> fetchUserLocation());

        postMemoryButton = rootView.findViewById(R.id.postMemoryButton);
        postMemoryButton.setOnClickListener(view -> postMusicMemory());
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (shouldClearData) {
            clearData();
        }

        shouldClearData = true;
    }

    private void setSelectedTrack(Track track) {
        songImageView.setVisibility(View.VISIBLE);
        Picasso.get().load(track.getAlbum().getImages()[0].getUrl()).into(songImageView);
        addSongButton.setText(track.getName());
    }

    private void goToSearchFragment() {
        shouldClearData = false;
        FragmentUtil.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.fragment_view,
                SearchFragment.class);
    }

    private void goToCameraActivity() {
        shouldClearData = false;
        Intent cameraIntent = new Intent(this.currentActivity, CameraActivity.class);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    @SuppressLint("MissingPermission")
    private void fetchUserLocation() {
        if (fusedLocationClient == null) {
            Message.showFailureMessage(currentActivity, "Google Play services is required");
            return;
        }

        if (locationPermission.isCoarseGranted() && locationPermission.isFineGranted()) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this.currentActivity, location -> {
                        if (location == null) {
                            Log.i(TAG, "Location unknown");
                            return;
                        }

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

    private void postMusicMemory() {
        if (SearchFragment.getResultTrack() == null) {
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

        postMemoryButton.setEnabled(false);

        String authorID = this.currentSession.getCurrentUser().getUid();
        Date timePosted = Calendar.getInstance().getTime();
        GeoPoint geoPointLocation = new GeoPoint(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );

        Song song = new Song(
                SearchFragment.getResultTrack().getArtists()[0].getName(),
                SearchFragment.getResultTrack().getName(),
                SearchFragment.getResultTrack().getArtists()[0].getId(),
                SearchFragment.getResultTrack().getAlbum().getImages()[0].getUrl(),
                SearchFragment.getResultTrack().getPreviewUrl()
        );

        Actions.uploadMusicMemoryImage(capturedImage, authorID).addOnCompleteListener(task -> {
            String imageUrl = task.getResult().toString();
            Actions.postMusicMemory(new MusicMemory(
                    authorID,
                    timePosted,
                    geoPointLocation,
                    imageUrl,
                    song
            )).whenCompleteAsync((unused, throwable) -> {
                if (throwable != null) {
                    Log.e(TAG, "Could not create music memory", throwable);
                    Message.showFailureMessage(this.currentActivity, "Could not create the music memory");
                    postMemoryButton.setEnabled(true);
                } else {
                    clearData();
                    FragmentUtil.replaceFragment(
                            requireActivity().getSupportFragmentManager(),
                            R.id.fragment_view,
                            FeedFragment.class
                    );
                    Message.showSuccessMessage(this.currentActivity, "Successfully created the music memory");
                }
            }, ContextCompat.getMainExecutor(requireContext()));
        });
    }

    private void clearData() {
        currentLocation = null;
        capturedImage = null;
        SearchFragment.setResultTrack(null);
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