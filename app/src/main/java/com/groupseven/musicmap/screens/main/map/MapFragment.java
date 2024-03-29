package com.groupseven.musicmap.screens.main.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.groupseven.musicmap.MusicMap;
import com.groupseven.musicmap.R;
import com.groupseven.musicmap.util.ui.map.CurrentLocationOverlay;
import com.groupseven.musicmap.util.permissions.LocationPermission;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayManager;

/**
 * Fragment containing an osmdroid map.
 */
public abstract class MapFragment extends Fragment {

    private static final String MULTITOUCH_FEATURE = "android.hardware.touchscreen.multitouch";

    // Keys for the map's shared preferences
    private static final String SHARED_PREFERENCE_ZOOM = "zoom";
    private static final String SHARED_PREFERENCE_CENTER_LATITUDE = "center_latitude";
    private static final String SHARED_PREFERENCE_CENTER_LONGITUDE = "center_longitude";

    // A bounding box containing the Netherlands
    private static final BoundingBox NETHERLANDS_BOUNDING_BOX = new BoundingBox(
            53.5104033474, 7.2294516, 50.803721015, 3.31497114423
    );

    private final LocationPermission locationPermission = new LocationPermission(this);

    // SharedPreferences instance depends on the map
    private final SharedPreferences sharedPreferences =
            MusicMap.getInstance().getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);

    /**
     * The MapView used by this fragment.
     *
     * @see #addOverlays() on how to add overlays to this map view.
     */
    private MapView mapView;

    /**
     * Make sure to call in the lifecycle after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has been called.
     *
     * @return the map view.
     */
    protected MapView getMapView() {
        return mapView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = requireActivity();

        // Load and initialize configuration of osmdroid
        Context ctx = activity.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // Request needed permissions if needed
        locationPermission.request();

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = rootView.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK); // the default OSM tile source (data source)

        // Set minimum zoom level, disallows for having multiple earth maps shown at once
        mapView.setMinZoomLevel(4.0);

        // Set zoom buttons location
        mapView.getZoomController().getDisplay().setPositions(false,
                CustomZoomButtonsDisplay.HorizontalPosition.RIGHT,
                CustomZoomButtonsDisplay.VerticalPosition.CENTER);

        if (getContext() == null || getContext().getPackageManager().hasSystemFeature(MULTITOUCH_FEATURE)) {
            // If multitouch is present, enable zooming with multitouch
            mapView.setMultiTouchControls(true);

            // and display zoom buttons only when needed
            mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);

            // buttons remain for 2.5s after scrolling, fading out takes 0.5s
            mapView.getZoomController().setShowFadeOutDelays(2500, 500);
        } else {
            // If multitouch absent, always enable zoom buttons
            mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        }

        // Add all overlays
        addOverlays();

        boolean interactionAllowed = allowInteraction();

        if (!interactionAllowed) {
            mapView.getZoomController().setZoomInEnabled(false);
            mapView.getZoomController().setZoomOutEnabled(false);
            mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
            mapView.setOnTouchListener((v, event) -> true);
        }

        if (sharedPreferences.contains(SHARED_PREFERENCE_ZOOM) && interactionAllowed) {
            // SharedPreferences cannot store double directly, use long instead
            double zoom = Double.longBitsToDouble(sharedPreferences.getLong(SHARED_PREFERENCE_ZOOM, 0));
            double longitude = Double.longBitsToDouble(sharedPreferences.getLong(SHARED_PREFERENCE_CENTER_LATITUDE, 0));
            double latitude = Double.longBitsToDouble(sharedPreferences.getLong(SHARED_PREFERENCE_CENTER_LONGITUDE, 0));

            // Restore stored zoom level & map center
            mapView.getController().setZoom(zoom);

            IGeoPoint center = new GeoPoint(longitude, latitude);
            mapView.getController().setCenter(center);
        } else {
            // Set initial view to map of Netherlands
            mapView.addOnFirstLayoutListener((v, left, top, right, bottom) ->
                    mapView.zoomToBoundingBox(NETHERLANDS_BOUNDING_BOX, false, 16));
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        // Store current zoom level & map center
        IGeoPoint mapCenter = mapView.getMapCenter();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        // SharedPreferences cannot store double directly, use long instead
        editor.putLong(SHARED_PREFERENCE_ZOOM, Double.doubleToRawLongBits(mapView.getZoomLevelDouble()));
        editor.putLong(SHARED_PREFERENCE_CENTER_LATITUDE, Double.doubleToRawLongBits(mapCenter.getLatitude()));
        editor.putLong(SHARED_PREFERENCE_CENTER_LONGITUDE, Double.doubleToRawLongBits(mapCenter.getLongitude()));
        editor.apply();
    }

    /**
     * Can be overridden to hide the current location marker.
     *
     * @return whether the current phone location should be displayed on the map.
     */
    protected boolean shouldDisplayCurrentLocation() {
        return true;
    }

    /**
     * Can be overridden to disable interaction with the map.
     *
     * @return whether the map can be interacted with.
     */
    protected boolean allowInteraction() {
        return true;
    }

    /**
     * Can be overridden to add overlays.
     * <p>
     * If a subclass wants to add an overlay, it should do so by overriding this method
     * and using {@link #addOverlay(Overlay)} with the overlay to add.
     */
    protected void addOverlays() {
        // Default overlay showing current location (only if needed)
        if (shouldDisplayCurrentLocation()) {
            CurrentLocationOverlay currentLocationOverlay = new CurrentLocationOverlay(mapView);
            addOverlay(currentLocationOverlay);
        }
    }

    /**
     * Adds the given overlay to the {@link #mapView}.
     *
     * @param overlay the overlay to add.
     */
    protected void addOverlay(Overlay overlay) {
        OverlayManager overlayManager = mapView.getOverlayManager();
        overlayManager.add(overlayManager.size(), overlay);
    }

}