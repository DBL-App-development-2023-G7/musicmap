package com.example.musicmap.screens.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PostFragment extends MainFragment {

    // needs to be static or data is lost
    // TODO find a better way of persisting data
    private static Bitmap capturedImage;
    private static final String TAG = "PostFragment";

    private final LocationPermission locationPermission = new LocationPermission(this);
    private final CameraPermission cameraPermission = new CameraPermission(this);
    private FusedLocationProviderClient fusedLocationClient;

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

                    CompletableFuture.runAsync(() -> {
                        try {
                            capturedImage = Picasso.get().load(imageUri)
                                    .rotate(ImageUtils.getImageRotationFromEXIF(parentActivity, imageUri))
                                    .get();
                        } catch (IOException e) {
                            Log.d(TAG, "Exception occurred while setting the image", e);
                        }
                    }).thenAcceptAsync(unused -> {
                        capturedImagePreview.setImageBitmap(capturedImage);
                        capturedImagePreview.setVisibility(View.VISIBLE);
                    }, requireActivity().getMainExecutor());
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationPermission.request();
        cameraPermission.request();
        getPermission();
        parentActivity = (SpotifyAuthActivity) requireActivity();
        Session session = Session.getInstance();
        CompletableFuture.runAsync(() -> {
            while (!session.isUserLoaded()) {
            }
        }).thenAccept(unused -> {
            parentActivity.refreshToken(
                    token -> Log.d("debug", String.format("[poop] Acess token %s", token)),
                    () -> ((SpotifyAuthActivity) requireActivity()).registerForSpotifyPKCE()
            );
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (capturedImage == null) {
            Log.d("debug", String.format("[poop] No image!"));
        }
        Log.d("debug", "[poop] Fragment start!");
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
        addLocationButton = rootView.findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(view -> fetchUserLocation());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fetchUserLocation();
        Button postMemoryButton = rootView.findViewById(R.id.postMemoryButton);
        postMemoryButton.setOnClickListener(view -> postMusicMemory());
        return rootView;
    }

    private void goToSearchFragment() {
        FragmentUtil.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.fragment_view,
                SearchFragment.class);
    }

    private void goToCameraActivity() {
        Intent cameraIntent = new Intent(requireActivity(), CameraActivity.class);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    @SuppressLint("MissingPermission")
    private void fetchUserLocation() {
        Log.d("debug", "[poop] call made!");
        if (locationPermission.isCoarseGranted() && locationPermission.isFineGranted()) {
            Log.d("debug", "[poop] permission Granted!");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        currentLocation = location;
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        Geocoder geocoder = new Geocoder(requireActivity());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                            if (addressList.size() > 0) {
                                Address address = addressList.get(0);
                                List<String> addressFeatures = new ArrayList<>();

                                addressFeatures.add(address.getThoroughfare());
                                addressFeatures.add(address.getLocality());
                                addressFeatures.add(address.getPremises());
                                addressFeatures.add(address.getSubAdminArea());
                                addressFeatures.add(address.getCountryName());

                                for (String f : addressFeatures) {
                                    Log.d("debug", String.format("[poop] Feature: %s", f));
                                }

                                String displayText = addressFeatures.stream()
                                        .filter(Objects::nonNull)
                                        .limit(2)
                                        .collect(Collectors.joining(", "));

                                addLocationButton.setText(displayText);
                            }
                        } catch (IOException e) {
                            Log.d("debug", "[poop] geocoder Exception!");
                        }
                    });
        } else {
            // TODO here we add error message
            Log.d("debug", "[poop] No location permission granted!");
        }
    }

    // TODO Make this monster prettier
    // Below is some questionable code
    private void postMusicMemory() {
        if (SearchFragment.resultTrack == null) {
            Log.d("debug", "[poop] Missing Track!!");
            return;
        }

        if (currentLocation == null) {
            Log.d("debug", "[poop] Missing Location!");
            return;
        }

        Session currentSession = Session.getInstance();
        String authorID = currentSession.getCurrentUser().getUid();

        Date timePosted = Calendar.getInstance().getTime();
        GeoPoint geoPointLocation = new GeoPoint(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );

        Song song = new Song(
                SearchFragment.resultTrack.getName(),
                SearchFragment.resultTrack.getArtists()[0].getId(),
                null, // sadly it is null //TODO why?
                SearchFragment.resultTrack.getAlbum().getImages()[0].getUrl(),
                SearchFragment.resultTrack.getPreviewUrl()
        );

        if (capturedImage != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference rootReference = FirebaseStorage.getInstance().getReference();
            String uuid = UUID.randomUUID().toString();
            StorageReference imageRef = rootReference.child(String.format("users/%s/memories/%s.jpg", authorID, uuid));
            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    Log.d("debug", "[poop] Failed image upload!");
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                String imageUrl = task.getResult().toString();
                Log.d("debug", String.format("[poop] Image uploaded! %s", imageUrl));
                Actions.postMusicMemory(new MusicMemory(
                        authorID,
                        timePosted,
                        geoPointLocation,
                        imageUrl,
                        song
                )).addOnFailureListener(e ->
                        Log.d("debug", String.format("[poop] Memory failed to upload! %s", e.getMessage()))
                ).addOnCompleteListener(unused -> {
                            clearData();
                            FragmentUtil.replaceFragment(
                                    requireActivity().getSupportFragmentManager(),
                                    R.id.fragment_view,
                                    FeedFragment.class
                            );
                            Log.d("debug", String.format("[poop] Successful upload!"));
                        }
                );
            });
        } else {
            Actions.postMusicMemory(new MusicMemory(
                    authorID,
                    timePosted,
                    geoPointLocation,
                    song.getImageUri().toString(),
                    song
            )).addOnFailureListener(e ->
                    Log.d("debug", String.format("[poop] Memory failed to upload! %s", e.getMessage()))
            ).addOnCompleteListener(unused ->
                    Log.d("debug", String.format("[poop] Successful upload!"))
            );

            clearData();
            FragmentUtil.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.fragment_view,
                    FeedFragment.class);
        }
    }

    private void clearData() {
        currentLocation = null;
        capturedImage = null;
        SearchFragment.resultTrack = null;
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        }
    }

}