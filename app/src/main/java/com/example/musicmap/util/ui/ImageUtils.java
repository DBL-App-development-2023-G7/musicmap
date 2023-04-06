package com.example.musicmap.util.ui;

import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.TAG_ORIENTATION;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    public static float getImageRotationFromEXIF(Activity activity, Uri imageUri) throws IOException {
        Log.w(TAG, String.format("Start: %s", imageUri.toString()));
        float rotation = 0f;
        ExifInterface exifInterface = new ExifInterface(activity.getContentResolver().openInputStream(imageUri));
        int orientation = exifInterface.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90: {
                rotation = 90f;
                break;
            }
            case ExifInterface.ORIENTATION_ROTATE_180: {
                rotation = 180f;
                break;
            }
            case ExifInterface.ORIENTATION_ROTATE_270: {
                rotation = -90f;
                break;
            }
            default:
                break;
        }
        Log.w(TAG, String.format("End: %s", imageUri.toString()));
        Log.w(TAG, String.format("End: .2%f", rotation));
        return rotation;
    }
}
