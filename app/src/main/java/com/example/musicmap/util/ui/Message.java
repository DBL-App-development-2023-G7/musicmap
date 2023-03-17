package com.example.musicmap.util.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.example.musicmap.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * Message class to show customisable Snackbars, with some common built-in ones.
 */
public final class Message {

    public static final Integer SHORT_DURATION = Snackbar.LENGTH_SHORT;
    public static final Integer LONG_DURATION = Snackbar.LENGTH_LONG;
    public static final Integer INDEFINITE_DURATION = Snackbar.LENGTH_INDEFINITE;

    /**
     * Shows failure message with view.
     *
     * @param view    the view
     * @param message the message
     */
    public static void showFailureMessage(ViewGroup view, String message) {
        builder().setView(view)
                .setText(message)
                .setDuration(Message.LONG_DURATION)
                .setActionText("Ok")
                .setActionTextColor(Color.WHITE)
                .failure()
                .show();
    }

    /**
     * Show failure message with activity.
     *
     * @param activity the activity
     * @param message  the message
     */
    public static void showFailureMessage(Activity activity, String message) {
        builder().setActivity(activity)
                .setText(message)
                .setDuration(Message.LONG_DURATION)
                .setActionText("Ok")
                .setActionTextColor(Color.WHITE)
                .failure()
                .show();
    }

    /**
     * Show success message with view.
     *
     * @param view    the view
     * @param message the message
     */
    public static void showSuccessMessage(ViewGroup view, String message) {
        builder().setView(view)
                .setText(message)
                .setDuration(Message.LONG_DURATION)
                .setActionText("Ok")
                .setActionTextColor(Color.WHITE)
                .success()
                .show();
    }

    /**
     * Show success message with activity.
     *
     * @param activity the activity
     * @param message  the message
     */
    public static void showSuccessMessage(Activity activity, String message) {
        builder().setActivity(activity)
                .setText(message)
                .setDuration(Message.LONG_DURATION)
                .setActionText("Ok")
                .setActionTextColor(Color.WHITE)
                .success()
                .show();
    }

    private enum Type {
        Default(null, null, null, null),
        Success(Color.parseColor("#2c5140"), R.drawable.baseline_check_24, Color.WHITE, "SUCCESS"),
        Failure(Color.parseColor("#8B0000"), R.drawable.baseline_cancel_24, Color.WHITE, "ERROR");

        private final Integer color;
        private final Integer iconResId;
        private final Integer standardTextColor;
        private final CharSequence text;

        Type(@ColorInt Integer color, @DrawableRes Integer iconResId,
             @ColorInt Integer standardTextColor, CharSequence text) {
            this.color = color;
            this.iconResId = iconResId;
            this.standardTextColor = standardTextColor;
            this.text = text;
        }

        /**
         * Gets the color.
         *
         * @return the color
         */
        public Integer getColor() {
            return color;
        }

        /**
         * Gets the icon.
         *
         * @param context the context
         * @return the icon
         */
        public Drawable getIcon(Context context) {
            if (iconResId == null) {
                return null;
            }

            return ContextCompat.getDrawable(context, iconResId);
        }

        /**
         * Gets the standard text color.
         *
         * @return the standard text color
         */
        public Integer getStandardTextColor() {
            return standardTextColor;
        }

        /**
         * Gets the text.
         *
         * @return the text
         */
        public CharSequence getText() {
            return text;
        }
    }

    private final Builder builder;

    /**
     * Constructor for builder.
     *
     * @param builder the builder
     */
    private Message(Builder builder) {
        this.builder = builder;
    }

    /**
     * Actually builds the Snackbar to show.
     *
     * @return the Snackbar
     */
    private Snackbar make() {
        if (builder.text == null) {
            builder.text = builder.type.getText();
        }

        Snackbar message = Snackbar.make(builder.view, builder.text, builder.duration);

        if (builder.actionClickListener != null || builder.actionText != null) {
            if (builder.actionClickListener == null) {
                builder.actionClickListener = v -> {
                };
            }

            if (builder.actionTextColor != null) {
                message.setActionTextColor(builder.actionTextColor);
            }

            message.setAction(builder.actionText, builder.actionClickListener);
        }

        Snackbar.SnackbarLayout messageLayout = (Snackbar.SnackbarLayout) message.getView();

        if (builder.backgroundColor == null) {
            builder.backgroundColor = builder.type.getColor();
        }

        if (builder.backgroundColor != null) {
            messageLayout.setBackgroundColor(builder.backgroundColor);
        }

        TextView text = messageLayout.findViewById(com.google.android.material.R.id.snackbar_text);

        if (builder.textColor != null) {
            text.setTextColor(builder.textColor);
        }

        if (builder.icon == null) {
            builder.icon = builder.type.getIcon(builder.view.getContext());
        }

        // show message over keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) builder.getView().getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(builder.getView().getWindowToken(), 0);

        return message;
    }

    /**
     * Returns a Builder object.
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    // CSOFF: FinalClass
    public static class Builder {
        // CSON: FinalClass
        private View view = null;
        private Type type = Type.Default;
        private int duration = Snackbar.LENGTH_SHORT;
        private CharSequence text = null;
        private Integer textColor = null;
        private Integer actionTextColor = null;
        private CharSequence actionText = "";
        private View.OnClickListener actionClickListener = null;
        private Drawable icon = null;
        private int iconResId = 0;
        private Integer backgroundColor = null;

        private Builder() {
        }

        /**
         * Sets activity.
         *
         * @return Builder
         */
        public Builder setActivity(Activity activity) {
            return setView(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0));
        }

        /**
         * Gets the view.
         *
         * @return the view
         */
        public View getView() {
            return this.view;
        }

        /**
         * Sets the view.
         *
         * @param view the view
         * @return Builder
         */
        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        /**
         * Sets the text.
         *
         * @param text the text
         * @return Builder
         */
        public Builder setText(CharSequence text) {

            this.text = text;
            return this;
        }

        /**
         * Sets the text color.
         *
         * @param color the text color
         * @return Builder
         */
        public Builder setTextColor(@ColorInt int color) {
            this.textColor = color;
            return this;
        }

        /**
         * Sets the action text.
         *
         * @param text the text
         * @return Builder
         */
        public Builder setActionText(CharSequence text) {
            this.actionText = text;
            return this;
        }

        /**
         * Sets the action text color.
         *
         * @param color the color for actionText
         * @return Builder
         */
        public Builder setActionTextColor(@ColorInt int color) {
            this.actionTextColor = color;
            return this;
        }

        /**
         * Sets the action click listener.
         *
         * @param listener the listener
         * @return Builder
         */
        public Builder setActionClickListener(View.OnClickListener listener) {
            this.actionClickListener = listener;
            return this;
        }

        /**
         * Sets the duration.
         *
         * @param duration duration of message
         * @return Builder
         */
        public Builder setDuration(@BaseTransientBottomBar.Duration int duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Sets the icon.
         *
         * @param resId the id of icon
         * @return Builder
         */
        public Builder setIcon(@DrawableRes int resId) {
            this.iconResId = resId;
            return this;
        }

        /**
         * Sets the icon.
         *
         * @param drawable the drawable icon
         * @return Builder
         */
        public Builder setIcon(Drawable drawable) {
            this.icon = drawable;
            return this;
        }

        /**
         * Sets the background color.
         *
         * @param color the background color
         * @return Builder
         */
        public Builder setBackgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        /**
         * Builds the Snackbar.
         *
         * @return Snackbar
         */
        public Snackbar build() {
            return make();
        }

        /**
         * Makes success Snackbar.
         *
         * @return Snackbar
         */
        public Snackbar success() {
            type = Type.Success;
            return make();
        }

        /**
         * Makes failure Snackbar.
         *
         * @return Snackbar
         */
        public Snackbar failure() {
            type = Type.Failure;
            return make();
        }

        /**
         * The method to make the Snackbar.
         *
         * @return Snackbar
         */
        private Snackbar make() {
            if (view == null) {
                throw new IllegalStateException("You must set an Activity or a View before making a message");
            }

            if (iconResId != 0) {
                icon = ContextCompat.getDrawable(view.getContext(), iconResId);
            }

            return new Message(this).make();
        }
    }

}


