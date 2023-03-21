package com.example.musicmap.screens.main;

import android.Manifest;
import android.app.Activity;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.musicmap.R;

import java.io.IOException;


public class PostFragment extends MainFragment {

    ImageView capturedImagePreview; // Should this be private?
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d("debug", "poop2");
                Uri imageUri = result.getData().getData(); // peak code right here
                try {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[] {Manifest.permission.CAMERA},
                    100
            );
        }

        capturedImagePreview = rootView.findViewById(R.id.previewCapturedImage);

        Button addImageButton = rootView.findViewById(R.id.addImageButton); // should this also be defined?
        addImageButton.setOnClickListener(view -> goToCameraFragment());
        Log.d("debug", "shit");
        return rootView;
    }

    private void startCameraActivity() {

    }

    private  void goToCameraFragment() {
       Log.d("debug", "dick");
       Intent cameraIntent = new Intent(requireActivity(), CameraActivity.class);
       cameraActivityResultLauncher.launch(cameraIntent);
    }


}