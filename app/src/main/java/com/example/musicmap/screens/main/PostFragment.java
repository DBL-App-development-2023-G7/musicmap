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
    ActivityResultLauncher<Intent> loginResultLauncher =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("debug", String.format("[poop] callback!"));
                    Intent resultIntent = result.getData();

                    Log.d("debug", String.format("[poop] result? %s",result.toString()));
                    if(resultIntent == null){
                        Log.d("debug", String.format("[poop] result is null"));
                    }
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug", "[poop] Fragment create!");
        registerForSpotifyPKCE();
    }
    SpotifySession s;
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
    public static final int REQUEST_CODE_2 = 69420;
    private static final String codeChallenge = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";

    public void registerForSpotifyPKCE() {
        URI redirectUri = SpotifyHttpManager.makeUri(SpotifyData.getRedirectUri());
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(SpotifyData.getClientId())
                .setRedirectUri(redirectUri)
                .build();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodePKCEUri(codeChallenge).build();
        authorizationCodeUriRequest.executeAsync().thenAcceptAsync(uri -> {
            try {
                Log.d("debug", String.format("[poop] URI: %s", uri.toString()));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                loginResultLauncher.launch(browserIntent);
            } catch (Throwable e){
                Log.d("debug", String.format("[poop] Error: %s", e.getMessage()));
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("debug", String.format("[poop] callback!"));
        Log.d("debug", String.format("[poop] callback %s!", resultCode));
        if (requestCode == REQUEST_CODE_2){
            Log.d("debug", String.format("[poop] responce recieved!"));
            final String EXTRA_AUTH_RESPONSE = "EXTRA_AUTH_RESPONSE";
            final String RESPONSE_KEY = "response";
            // A lot of potential for errors here
            AuthorizationResponse response = intent.getBundleExtra(EXTRA_AUTH_RESPONSE).getParcelable(RESPONSE_KEY);
            switch (response.getType()) {
                case TOKEN:
                    String accessToken = response.getAccessToken();
                    Log.d("debug", String.format("[poop] Token: %s", accessToken));
                    SpotifyData.setToken(accessToken);
                    break;

                case ERROR:
                    Log.d("debug", String.format("[poop] Sign in failed %s", response.getError()));
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d("debug", String.format("[poop] default"));
                    // Handle other cases
            }

        }
    }

}