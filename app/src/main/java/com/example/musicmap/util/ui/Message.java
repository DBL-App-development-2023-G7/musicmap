package com.example.musicmap.util.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.example.musicmap.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class Message {

    private enum Type {
        Default(null, null, null, null),
        Success(Color.parseColor("#388E3C"), R.drawable.baseline_check_24, Color.WHITE, "SUCCESS"),
        Failure(Color.parseColor("#D50000"), R.drawable.baseline_cancel_24, Color.WHITE, "ERROR");

        private final Integer color;
        private final Integer iconResId;
        private final Integer standardTextColor;
        private final CharSequence text;

        Type(@ColorInt Integer color, @DrawableRes Integer iconResId, @ColorInt Integer standardTextColor, CharSequence text) {
            this.color = color;
            this.iconResId = iconResId;
            this.standardTextColor = standardTextColor;
            this.text = text;
        }

        public Integer getColor() {
            return color;
        }

        public Drawable getIcon(Context context) {
            if (iconResId == null) {
                return null;
            }

            return ContextCompat.getDrawable(context, iconResId);
        }


        public Integer getStandardTextColor() {
            return standardTextColor;
        }

        public CharSequence getText() {
            return text;
        }
    }

    private final Builder builder;

    private Message(Builder builder) {
        this.builder = builder;
    }

    private Snackbar make() {
        if (builder.text == null) {
            builder.text = builder.type.getText();
        }

        Snackbar message = Snackbar.make(builder.view, builder.text, builder.duration);

        if (builder.actionClickListener != null || builder.actionText != null) {
            if (builder.actionClickListener == null)
                builder.actionClickListener = v -> {};

            message.setAction(builder.actionText, builder.actionClickListener);
        }

        Snackbar.SnackbarLayout messageLayout = (Snackbar.SnackbarLayout) message.getView();

        if (builder.backgroundColor == null) {
            builder.backgroundColor = builder.type.getColor();
        }

        if (builder.backgroundColor != null) {
            messageLayout.setBackgroundColor(builder.backgroundColor);
        }

        if (builder.icon == null) {
            builder.icon = builder.type.getIcon(builder.view.getContext());
        }

        return message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private View view = null;
        private Type type = Type.Default;
        private int duration = Snackbar.LENGTH_SHORT;
        private CharSequence text = null;
        private CharSequence actionText = "";
        private View.OnClickListener actionClickListener = null;
        private Drawable icon = null;
        private Integer backgroundColor = null;

        public View getView() {
            return view;
        }

        public Type getType() {
            return type;
        }

        public int getDuration() {
            return duration;
        }

        public CharSequence getText() {
            return text;
        }

        public CharSequence getActionText() {
            return actionText;
        }

        public View.OnClickListener getActionClickListener() {
            return actionClickListener;
        }

        public Drawable getIcon() {
            return icon;
        }

        public Integer getBackgroundColor() {
            return backgroundColor;
        }

        private Builder() {}

        public Builder setActivity(Activity activity) {
            return setView(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0));
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setText(CharSequence text) {
            this.text = text;
            return this;
        }

        public Builder setActionText(CharSequence text) {
            this.actionText = text;
            return this;
        }

        public Builder setActionClickListener(View.OnClickListener listener) {
            this.actionClickListener = listener;
            return this;
        }

        public Builder setDuration(@BaseTransientBottomBar.Duration int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setIcon(Drawable drawable) {
            this.icon = drawable;
            return this;
        }

        public Builder setBackgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        public Snackbar build() {
            return make();
        }

        public Snackbar success() {
            type = Type.Success;
            return make();
        }

        public Snackbar failure() {
            type = Type.Failure;
            return make();
        }

        private Snackbar make() {
            if (view == null) {
                throw new IllegalStateException("You must set an Activity or a View before making a snack");
            }

            return new Message(this).make();
        }
    }
}


