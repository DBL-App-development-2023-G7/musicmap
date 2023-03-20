package com.example.musicmap.util.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * A utility class that provides helper methods for Message class.
 */
public class MessageUtils {

    /**
     * Creates a transparent drawable with the specified width and height using the
     * provided context.
     *
     * @param context the context to use for creating the drawable
     * @param width the width of the drawable, in pixels
     * @param height the height of the drawable, in pixels
     * @return a transparent drawable with the specified width and height
    */
    static Drawable makeTransparentDrawable(Context context, int width, int height) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(width, height, conf);
        return new BitmapDrawable(context.getResources(), bmp);
    }

}
