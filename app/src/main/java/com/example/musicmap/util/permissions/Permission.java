package com.example.musicmap.util.permissions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.example.musicmap.MusicMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A utility class for dealing with runtime (i.e. dangerous) permissions.
 */
public abstract class Permission {

    private static final String SHARED_PREFERENCES_PREFIX = "permission_";
    private static final String SHARED_PREFERENCES_KEY = "requested";

    private final List<Runnable> onChangeListeners = new ArrayList<>();

    private final ActivityResultLauncher<String[]> launcher;
    private final SharedPreferences sharedPreferences;

    /**
     * Creates a Permission instance.
     *
     * @param activityResultCaller an instance such as
     *                             a {@link androidx.fragment.app.Fragment}
     *                             or {@link androidx.appcompat.app.AppCompatActivity}.
     */
    protected Permission(ActivityResultCaller activityResultCaller) {
        launcher = activityResultCaller.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::onActivityResult);

        String sharedPreferencesName = SHARED_PREFERENCES_PREFIX + getClass().getSimpleName().toLowerCase(Locale.ROOT);
        sharedPreferences = MusicMap.getInstance().getSharedPreferences(sharedPreferencesName,
                Context.MODE_PRIVATE);
    }

    protected abstract String[] getAndroidPermissions();

    private void onActivityResult(Map<String, Boolean> permissions) {
        onChangeListeners.forEach(Runnable::run);
    }

    /**
     * Gets the permission grant map.
     *
     * This map contains, for each {@link #getAndroidPermissions() permission}, a boolean
     * indication whether that permission was granted to the app.
     *
     * @return the permission grant map.
     */
    protected final Map<String, Boolean> getPermissionGrantMap() {
        // Gets the context
        Context context = MusicMap.getInstance().getApplicationContext();

        Map<String, Boolean> permissionGrantMap = new HashMap<>();
        for (String permission : getAndroidPermissions()) {
            // Check if permission is granted
            boolean granted = ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;

            permissionGrantMap.put(permission, granted);
        }

        return permissionGrantMap;
    }

    /**
     * Requests this permission.
     *
     * Feel free to use even if the permission has already been granted:
     * the user will only receive the request once.
     *
     * @see #forceRequest()
     */
    public void request() {
        // Check if permissions were requested before
        if (!sharedPreferences.getBoolean(SHARED_PREFERENCES_KEY, false)) {
            forceRequest();
        }
    }

    /**
     * Requests this permissions. This will not check if the permission was requested before.
     *
     * Use this over {@link #request()} in case the action causing the request
     * is directly linked to the permission, e.g. a button 'take picture' is directly linked to camera permissions.
     */
    public void forceRequest() {
        Log.i("MM-Permission", "Requested " + getClass().getSimpleName());

        this.launcher.launch(getAndroidPermissions());

        // Store the request in the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARED_PREFERENCES_KEY, true);
        editor.apply();
    }

    /**
     * Adds a listener that will be called when this permission's status is changed,
     * i.e. when it is granted or denied.
     *
     * @param runnable the runnable to run when the status changed.
     */
    public void onChange(Runnable runnable) {
        onChangeListeners.add(runnable);
    }

}
