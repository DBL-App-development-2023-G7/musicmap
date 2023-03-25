package com.example.musicmap.util.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

import com.example.musicmap.R;
import com.example.musicmap.feed.Post;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.IconOverlay;

/**
 * A overlay containing a {@link Post}.
 *
 * Abstract class, since the image provided depends on the type of post.
 *
 * @see MusicMemoryOverlay
 */
public abstract class PostOverlay extends IconOverlay {

    private static final String TAG = "PostOverlay";

    /**
     * The scale of the image relative to the icon.
     */
    private static final float RATIO = 0.875f;

    /**
     * How far from the left the image should start.
     */
    private static final float LEFT_MARGIN_RATIO = 0.0625f;

    /**
     * How far from the top the image should start.
     */
    private static final float TOP_MARGIN_RATIO = 0.045f;

    private final MapView mapView;

    /**
     * Create a post overlay for the given map and post, using the given {@link RequestCreator}
     * for the image.
     *
     * @param mapView the map.
     * @param post the post.
     * @param requestCreator the image request for the image to be displayed on the map.
     */
    protected PostOverlay(MapView mapView, Post post, RequestCreator requestCreator) {
        this.mapView = mapView;

        Drawable drawable = ContextCompat.getDrawable(mapView.getContext(), R.drawable.map_post);
        if (drawable == null) {
            throw new IllegalStateException("Drawable map_post could not be found");
        }

        // Get the GeoPoint from the right library
        GeoPoint geoPoint = post.getLocation();
        IGeoPoint iGeoPoint = new org.osmdroid.util.GeoPoint(geoPoint.getLatitude(), geoPoint.getLongitude());

        set(iGeoPoint, drawable);

        // Set anchor position to bottom center
        this.mAnchorV = ANCHOR_BOTTOM;
        this.mAnchorU = ANCHOR_CENTER;

        // Transform into circle & resize image
        requestCreator
                .resize((int) (drawable.getIntrinsicWidth() * RATIO), (int) (drawable.getIntrinsicHeight() * RATIO))
                .centerCrop()
                .transform(new CircleTransform())
                .into(new ImageTarget());
    }

    /**
     * Create a post overlay for the given map and post, using the image URI.
     *
     * @param mapView the map.
     * @param post the post.
     * @param imageUri the URI of the image to display on the map.
     */
    protected PostOverlay(MapView mapView, Post post, Uri imageUri) {
        this(mapView, post, Picasso.get().load(imageUri));
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        // TODO open post screen
        return true;
    }

    /**
     * A {@link Target} that will put the received image in the marker on the map.
     */
    public class ImageTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable iconDrawable = ContextCompat.getDrawable(mapView.getContext(), R.drawable.map_post);
            assert iconDrawable != null;

            int width = iconDrawable.getIntrinsicWidth();
            int height = iconDrawable.getIntrinsicHeight();

            // Create BitMap for final icon
            Bitmap finalBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());

            // Draw map_post on BitMap
            Canvas canvas = new Canvas(finalBitmap);
            iconDrawable.setBounds(0, 0, width, height);
            iconDrawable.draw(canvas);

            // Draw fetched image on BitMap
            canvas.drawBitmap(bitmap, LEFT_MARGIN_RATIO * width, TOP_MARGIN_RATIO * height, new Paint());

            // Set image
            // TODO run on UI thread?
            set(getPosition(), new BitmapDrawable(mapView.getResources(), finalBitmap));

            // Refresh overlay to display image
            mapView.postInvalidate();
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Log.e(TAG, "Could not load picture", e);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) { }
    }

}