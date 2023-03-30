package com.example.musicmap.util.permissions;

import android.Manifest;

import androidx.activity.result.ActivityResultCaller;

/**
 * Camera permissions.
 */
public class CameraPermission extends Permission {

    private static final String CAMERA = Manifest.permission.CAMERA;

    public CameraPermission(ActivityResultCaller activityResultCaller) {
        super(activityResultCaller);
    }

    @Override
    public String[] getAndroidPermissions() {
        return new String[]{Manifest.permission.CAMERA};
    }

    public boolean isGranted() {
        return Boolean.TRUE.equals(getPermissionGrantMap().get(CAMERA));
    }

}
