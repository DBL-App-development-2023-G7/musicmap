package com.groupseven.musicmap.util.permissions;

import android.Manifest;
import android.os.Build;

import androidx.activity.result.ActivityResultCaller;

/**
 * Media permissions, includes images and video.
 */
public class MediaPermission extends Permission {

    public MediaPermission(ActivityResultCaller activityResultCaller) {
        super(activityResultCaller);
    }

    @Override
    public String[] getAndroidPermissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            return new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }
    }

    public boolean isReadImagesGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            return Boolean.TRUE.equals(getPermissionGrantMap().get(Manifest.permission.READ_MEDIA_IMAGES));
        } else {
            return Boolean.TRUE.equals(getPermissionGrantMap().get(Manifest.permission.READ_EXTERNAL_STORAGE));
        }
    }

    public boolean isReadVideoGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            return Boolean.TRUE.equals(getPermissionGrantMap().get(Manifest.permission.READ_MEDIA_VIDEO));
        } else {
            return Boolean.TRUE.equals(getPermissionGrantMap().get(Manifest.permission.READ_EXTERNAL_STORAGE));
        }
    }

    public boolean isReadAllGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            return isReadImagesGranted() && isReadVideoGranted();
        } else {
            return Boolean.TRUE.equals(getPermissionGrantMap().get(Manifest.permission.READ_EXTERNAL_STORAGE));
        }
    }

}
