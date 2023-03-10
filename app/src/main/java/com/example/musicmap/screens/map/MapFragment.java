package com.example.musicmap.screens.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.musicmap.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapFragment extends Fragment {

    private static final String MULTITOUCH_FEATURE = "android.hardware.touchscreen.multitouch";

    private static final int PERMISSION_ACCESS_FINE_LOCATION_REQUEST_CODE = 1;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION_REQUEST_CODE = 2;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = requireActivity();

        // Load and initialize configuration of osmdroid
        Context ctx = activity.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // Request needed permissions if applicable
        // TODO message explaining the permissions,
        //  see https://developer.android.com/training/permissions/requesting#explain
        //  check if permissions were granted, check if the app can function without these permission
        //  probably do permissions elsewhere
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request coarse location access permission
            activity.requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION_REQUEST_CODE);
        }
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request fine location access permission
            activity.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION_REQUEST_CODE);
        }

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = rootView.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK); // the default OSM tile source (data source)

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

        // Set initial screen to a full view of the Netherlands
        // TODO start with current location at first open,
        //  and reset last controller's position
        mapView.getController().setCenter(new GeoPoint(52.132303, 5.645042));
        mapView.getController().setZoom(8.0);

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
     * A method that can be overridden to add overlays.
     *
     * If a subclass wants to add an overlay, it should do so by overriding this method,
     * using {@link #addOverlay(Overlay)}.
     */
    protected void addOverlays() {
        // Default overlay showing current location (only if needed)
        if (shouldDisplayCurrentLocation()) {
            CurrentLocationOverlay currentLocationOverlay = new CurrentLocationOverlay(mapView);
            mapView.getOverlayManager().add(0, currentLocationOverlay);
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

    /**
     * A map overlay showing your current location, but without bearing.
     */
    public static class CurrentLocationOverlay extends MyLocationNewOverlay {
        public CurrentLocationOverlay(MapView mapView) {
            super(mapView);

            // Overwrite default person icon
            Resources resources = mapView.getContext().getResources();
            setPersonIcon(BitmapFactory.decodeResource(resources, R.drawable.map_marker));
            setPersonAnchor(0.5f, 1f); // bottom middle
        }

        @Override
        protected void drawMyLocation(Canvas canvas, Projection pj, Location lastFix) {
            lastFix.removeBearing();

            super.drawMyLocation(canvas, pj, lastFix);
        }
    }

}