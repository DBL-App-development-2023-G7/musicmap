package com.groupseven.musicmap.screens.main.musicmemory.create;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.groupseven.musicmap.R;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.listeners.SessionListenerActivity;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.screens.main.MainFragment;
import com.groupseven.musicmap.screens.main.feed.FeedFragment;
import com.groupseven.musicmap.util.firebase.Actions;
import com.groupseven.musicmap.util.permissions.CameraPermission;
import com.groupseven.musicmap.util.permissions.LocationPermission;
import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.groupseven.musicmap.util.ui.FragmentUtil;
import com.groupseven.musicmap.util.ui.ImageUtils;
import com.groupseven.musicmap.util.ui.Message;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostFragment extends MainFragment {

    private static final String TAG = "PostFragment";

    private final LocationPermission locationPermission = new LocationPermission(this);
    private final CameraPermission cameraPermission = new CameraPermission(this);
    private FusedLocationProviderClient fusedLocationClient;
    public static final String FRAGMENT_RESULT_KEY = "searchSong";

    private Session currentSession;
    private SessionListenerActivity parentActivity;
    private ImageView songImageView;
    private Button addSongButton;
    private Button addLocationButton;
    private ImageView capturedImagePreview;
    private Button postMemoryButton;
    private boolean shouldClearData = true;

    private PostViewModel model;

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

    private SpotifyAccess spotifyAccess;

    // this is a Picasso target into which Picasso will load the image taken from the camera
    // in a field so it won't be garbage collected
    private final Target cameraImageTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d(TAG, "Camera Image Bitmap Loaded");
            model.getCameraImage().setValue(bitmap);
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
        this.parentActivity = (SessionListenerActivity) requireActivity();
        this.spotifyAccess = SpotifyAccess.getSpotifyAccessInstance();

        locationPermission.forceRequest();
        cameraPermission.forceRequest();

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(parentActivity)
                == ConnectionResult.SUCCESS) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(parentActivity);
        } else {
            int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(parentActivity);
            Log.e(TAG, "Google Play services availability response: " + response);
        }

        model = new ViewModelProvider(parentActivity).get(PostViewModel.class);

        fetchUserLocation();

        model.setSongToCurrentUserSong();
        // Setup callback from search result fragment
        getParentFragmentManager().setFragmentResultListener(
                "searchSong",
                this,
                (requestKey, bundle) -> {
                    Log.d(TAG, "result!");
                    Song resultSong = (Song) bundle.getSerializable("song");
                    model.getSelectedSong().setValue(resultSong);
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        capturedImagePreview = rootView.findViewById(R.id.previewCapturedImage);
        model.getCameraImage().observe(this.getViewLifecycleOwner(), this::showCapturedImage);

        Button addImageButton = rootView.findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(view -> goToCameraActivity());

        addSongButton = rootView.findViewById(R.id.addSongButton);
        addSongButton.setOnClickListener(view -> goToSearchFragment());
        songImageView = rootView.findViewById(R.id.songPreviewImage);
        model.getSelectedSong().observe(this.getViewLifecycleOwner(), this::showSelectedSong);

        addLocationButton = rootView.findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(view -> fetchUserLocation());
        model.getUserLocation().observe(this.getViewLifecycleOwner(), this::showUserLocation);

        postMemoryButton = rootView.findViewById(R.id.postMemoryButton);
        postMemoryButton.setOnClickListener(view -> postMusicMemory());

        spotifyAccess.refreshToken(new SpotifyAccess.TokenCallback() {
            @Override
            public void onValidToken() {
                postMemoryButton.setEnabled(true);
            }

            @Override
            public void onInvalidToken() {
                Message.showFailureMessage(parentActivity, getString(R.string.error_spotify_not_connected));
                postMemoryButton.setEnabled(false);
            }
        });

        return rootView;
    }

    private void showUserLocation(Location location){
        if (location != null) {
            String locationText = getLocationText(location);
            addLocationButton.setText(locationText);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (shouldClearData) {
            model.clearData();
        }

        shouldClearData = true;
    }

    private void showCapturedImage(Bitmap capturedImage){
        if (capturedImage != null) {
            capturedImagePreview.setImageBitmap(capturedImage);
            capturedImagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void showSelectedSong(Song song) {
        if (song != null) {
            songImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(song.getImageUri()).into(songImageView);
            addSongButton.setText(song.getName());
        }
    }

    private void goToSearchFragment() {
        shouldClearData = false;
        FragmentUtil.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.fragment_view,
                SearchFragment.class);
    }

    private void goToCameraActivity() {
        shouldClearData = false;
        Intent cameraIntent = new Intent(this.parentActivity, CameraActivity.class);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    // TODO move to a utilities class (I tried but it is a huge pain please help)
    @SuppressLint("MissingPermission")
    private void fetchUserLocation() {
        if (fusedLocationClient == null) {
            Message.showFailureMessage(parentActivity, getString(R.string.gps_required));
            return;
        }
        if (locationPermission.isCoarseGranted() && locationPermission.isFineGranted()) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(parentActivity, location -> {
                        if (location == null) {
                            Log.i(TAG, "Location unknown");
                            return;
                        }
                        model.getUserLocation().setValue(location);
                    })
                    .addOnFailureListener(parentActivity, exception ->
                            Log.e(TAG, "Could not fetch user location", exception)
                    );
        } else {
            Message.showFailureMessage(parentActivity, getString(R.string.location_permission_not_granted));
        }
    }

    // Takes a location and returns a human readable string to display on the search bar
    private String getLocationText(Location location){
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        String resultString = String.format("Lat: %s \nLon: %s",
                location.getLatitude(),
                location.getLongitude()
        );
        Geocoder geocoder = new Geocoder(parentActivity);
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
        if (model.isSongNull()) {
            Message.showFailureMessage(this.parentActivity, getString(R.string.create_mm_track_required));
            return;
        }

        if (model.isLocationNull()) {
            Message.showFailureMessage(this.parentActivity, getString(R.string.create_mm_location_required));
            return;
        }

        if (model.isImageNull()) {
            Message.showFailureMessage(this.parentActivity, getString(R.string.create_mm_image_required));
            return;
        }

        postMemoryButton.setEnabled(false);

        String authorID = this.currentSession.getCurrentUser().getUid();
        Bitmap capturedImage = model.getCameraImage().getValue();

        Actions.uploadMusicMemoryImage(capturedImage, authorID).thenAccept(imageUrl ->
                Actions.postMusicMemory(new MusicMemory(
                    authorID,
                    Calendar.getInstance().getTime(),
                    model.getLocationAsGeoPoint(),
                    imageUrl.toString(),
                    model.getSelectedSong().getValue()
                )
        ).whenCompleteAsync((unused, throwable) -> {
            if (throwable != null) {
                Log.e(TAG, "Could not create music memory", throwable);
                Message.showFailureMessage(this.parentActivity, getString(R.string.create_mm_failure));
                postMemoryButton.setEnabled(true);
            } else {
                FragmentUtil.replaceFragment(
                        requireActivity().getSupportFragmentManager(),
                        R.id.fragment_view,
                        FeedFragment.class
                );
                model.clearData();
                Message.showSuccessMessage(this.parentActivity, getString(R.string.create_mm_success));
            }
        }, ContextCompat.getMainExecutor(requireContext())));
    }

}