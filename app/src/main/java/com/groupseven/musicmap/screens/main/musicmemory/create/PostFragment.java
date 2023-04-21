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
import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.groupseven.musicmap.util.firebase.Actions;
import com.groupseven.musicmap.util.permissions.CameraPermission;
import com.groupseven.musicmap.util.permissions.LocationPermission;
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

import javax.annotation.Nullable;

public class PostFragment extends MainFragment {

    private static final String TAG = "PostFragment";

    /**
     * The key to find the chosen song from {@link SearchFragment}.
     */
    public static final String SONG_RESULT_KEY = "searchSong";

    // Permissions for location and camera
    private final LocationPermission locationPermission = new LocationPermission(this);
    private final CameraPermission cameraPermission = new CameraPermission(this);

    @Nullable
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * The parent activity hosting this fragment.
     */
    private SessionListenerActivity parentActivity;

    private ImageView songImageView;
    private Button addSongButton;
    private Button addLocationButton;
    private ImageView capturedImagePreview;
    private Button postMemoryButton;

    // Data for switching between search and post fragment
    private PostViewModel model;
    private boolean shouldClearData = true;

    /**
     * Launcher that launches the camera activity and handles the result.
     */
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

                    // Load image preview
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

    /**
     * The {@link Target} used to load the image into the {@link PostViewModel}.
     * <p>
     * This must be a field, as otherwise Picasso allows it to be garbage collected.
     * According to them, this behaviour is a
     * <a href="https://github.com/square/picasso/issues/1667#issuecomment-327198003">feature, not a bug</a>.
     */
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

        this.parentActivity = (SessionListenerActivity) requireActivity();

        locationPermission.forceRequest();
        cameraPermission.forceRequest();

        // Initiate fusedLocationClient, first check if Google Play Services is available
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(parentActivity)
                == ConnectionResult.SUCCESS) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(parentActivity);
        } else {
            int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(parentActivity);
            Log.e(TAG, "Google Play services availability response: " + response);
        }

        model = new ViewModelProvider(parentActivity).get(PostViewModel.class);

        // Start fetching user's location
        fetchUserLocation();

        // Start fetching currently playing song
        model.setSongToCurrentUserSong();

        // Setup callback from search result fragment
        getParentFragmentManager().setFragmentResultListener(
                SONG_RESULT_KEY,
                this,
                (requestKey, bundle) -> {
                    Song resultSong = (Song) bundle.getSerializable(SONG_RESULT_KEY);
                    Log.d(TAG, "Song chosen: " + resultSong);

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

        // Check if Spotify is connected
        SpotifyAccess.getSpotifyAccessInstance().refreshToken(new SpotifyAccess.TokenCallback() {
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

    @Override
    public void onStop() {
        super.onStop();
        // Reset the data
        if (shouldClearData) {
            model.clearData();
        }

        shouldClearData = true;
    }

    /**
     * Updates the UI to display the given image preview.
     *
     * @param capturedImage the image to display.
     */
    private void showCapturedImage(Bitmap capturedImage) {
        if (capturedImage != null) {
            capturedImagePreview.setImageBitmap(capturedImage);
            capturedImagePreview.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the UI to display the given song.
     *
     * @param song the song to display.
     */
    private void showSelectedSong(Song song) {
        if (song != null) {
            songImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(song.getImageUri()).into(songImageView);
            addSongButton.setText(song.getName());
        }
    }

    /**
     * Updates the UI with the given location.
     *
     * @param location the location to display.
     */
    private void showUserLocation(Location location) {
        if (location != null) {
            String locationText = getLocationText(location);
            addLocationButton.setText(locationText);
        }
    }

    /**
     * Converts a location to a human readable string.
     * <p>
     * This method may block.
     *
     * @param location the location to convert.
     * @return the string describing the location.
     */
    private String getLocationText(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // A backup string of we cannot get a proper string format for the location
        String coordinateString = String.format("Lat: %s \nLon: %s",
                location.getLatitude(),
                location.getLongitude()
        );

        Geocoder geocoder = new Geocoder(parentActivity);
        try {
            // Fetch the addresses
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList.isEmpty()) {
                Log.w(TAG, "Geocoder could not find an associated address");
                return coordinateString;
            }

            Address address = addressList.get(0);
            List<String> addressFeatures = new ArrayList<>();

            // Add some address features
            addressFeatures.add(address.getThoroughfare());
            addressFeatures.add(address.getLocality());
            addressFeatures.add(address.getCountryName());

            // Select the 2 most prominent features of the location,
            //  filtering out unknown values
            return addressFeatures.stream()
                    .filter(Objects::nonNull)
                    .limit(2)
                    .collect(Collectors.joining(", "));
        } catch (IOException e) {
            Log.w(TAG, "Something went wrong while using Geocoder", e);
            return coordinateString;
        }
    }

    /**
     * Fetches the user's location, and adds it to the {@link #model}.
     */
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

                        // Add location to model
                        model.getUserLocation().setValue(location);
                    })
                    .addOnFailureListener(parentActivity, exception ->
                            Log.e(TAG, "Could not fetch user location", exception)
                    );
        } else {
            Message.showFailureMessage(parentActivity, getString(R.string.location_permission_not_granted));
        }
    }

    /**
     * Open the song search fragment so the user can select the song.
     */
    private void goToSearchFragment() {
        shouldClearData = false;
        FragmentUtil.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.fragment_view,
                SearchFragment.class);
    }

    /**
     * Open the camera activity so the user can take a photo.
     */
    private void goToCameraActivity() {
        shouldClearData = false;
        Intent cameraIntent = new Intent(this.parentActivity, CameraActivity.class);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    /**
     * Posts the music memory that is stored in {@link #model}.
     */
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

        String authorID = Session.getInstance().getCurrentUser().getUid();
        Bitmap capturedImage = model.getCameraImage().getValue();

        // Upload photo and music memory
        Actions.uploadMusicMemoryImage(capturedImage, authorID).thenCompose(imageUrl ->
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
                // Return back to FeedFragment
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