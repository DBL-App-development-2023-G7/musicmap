package com.example.musicmap.screens.main;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.musicmap.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

// maybe it should be an activity.. maybe a fragment?
public class CameraActivity extends AppCompatActivity {

    // the view of what the camera sees
    private PreviewView cameraPreviewView;
    // CameraX use case for capturing images
    private ImageCapture imageCaptureUseCase;
    private LifecycleCameraController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        controller = new LifecycleCameraController(this);
        controller.bindToLifecycle(this);

        cameraPreviewView = findViewById(R.id.cameraPreviewView);
        cameraPreviewView.setController(controller);
        ImageButton takePictureButton = findViewById(R.id.cameraCaptureButton);
        takePictureButton.setOnClickListener(view -> takePicture());
    }

    // Boilerplate code needed to setup cameraX
    private void setupCamera() {
        // create a cameraProvider which is basically fetching data about the device's camera
        // it may take a second that is why it is a Future
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // add listener when camera is setup
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // make sure the camera is not used anywhere else
                cameraProvider.unbindAll();
                // grab the back facing camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                // Setup CameraX camera Preview "Use case" in order to get a view of what the camera is seeing
                Preview previewUseCase = new Preview.Builder().build();
                previewUseCase.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());

                //Setup CameraX image capture "Use case" in order to capture an image
                imageCaptureUseCase = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();
                // bind the Use Cases tot the camera
                // and bind the camera to the activity lifecycle
                cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase, imageCaptureUseCase);
            } catch (ExecutionException e) {
                // no idea how to handle the exceptions
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

    }

    // Consider handling camera on a different executor
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void takePicture() {
        // take a timestamp to generate a unique file name
        long timeStamp = System.currentTimeMillis();
        String timeStampStr = Long.toString(timeStamp);
        // create a file to store the image
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStampStr);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        // in theory the file is in a temporary app folder
        // but this depends on the android version
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            String appName = getBaseContext().getString(R.string.app_name);
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, String.format("Pictures/%s", appName));
        }

        // tell camera X how you want to store your image
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions
                .Builder(getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();

        // actually call the take picture method
        controller.takePicture(
                outputOptions,
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.d("debug", "[poop] Image captured");
                        // return to post screen activity
                        Intent result = new Intent("com.example.RESULT_ACTION", outputFileResults.getSavedUri());
                        setResult(Activity.RESULT_OK, result);
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        // insert your code here.
                        // no code since we are cool as heck and an error will never happen
                    }
                }
        );
    }

}