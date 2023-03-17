package com.example.musicmap.util.permissions;

import android.Manifest;

import androidx.activity.result.ActivityResultCaller;

/**
 * Location permissions. Includes {@code COARSE_LOCATION} and {@code FINE_LOCATION}.
 */
public class LocationPermission extends Permission {

    private static final String FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE = Manifest.permission.ACCESS_COARSE_LOCATION;

    public LocationPermission(ActivityResultCaller activityResultCaller) {
        super(activityResultCaller);
    }

    @Override
    public String[] getAndroidPermissions() {
        return new String[]{COARSE, FINE};
    }

    public boolean isFineGranted() {
        return Boolean.TRUE.equals(getPermissionGrantMap().get(FINE));
    }

    public boolean isCoarseGranted() {
        return isFineGranted() || Boolean.TRUE.equals(getPermissionGrantMap().get(COARSE));
    }

    public boolean isNoneGranted() {
        return !isCoarseGranted();
    }

}
