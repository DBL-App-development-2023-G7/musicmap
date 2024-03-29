package com.groupseven.musicmap.util.ui.map;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;

import com.groupseven.musicmap.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * A map overlay showing your current location.
 */
public class CurrentLocationOverlay extends MyLocationNewOverlay {

    public CurrentLocationOverlay(MapView mapView) {
        super(mapView);

        // Overwrite default person icon
        Resources resources = mapView.getContext().getResources();
        setPersonIcon(BitmapFactory.decodeResource(resources, R.drawable.map_marker));
        setPersonAnchor(0.5f, 1f); // bottom middle
    }

    @Override
    protected void drawMyLocation(Canvas canvas, Projection pj, Location lastFix) {
        // Bearing causes a different image to be displayed, so remove bearing
        lastFix.removeBearing();

        super.drawMyLocation(canvas, pj, lastFix);
    }

}
