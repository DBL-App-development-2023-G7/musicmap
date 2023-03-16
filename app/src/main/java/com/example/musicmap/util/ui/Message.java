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

public class Message {

    public static final Integer SHORT_DURATION = Snackbar.LENGTH_SHORT;
    public static final Integer LONG_DURATION = Snackbar.LENGTH_LONG;
    public static final Integer INDEFINITE_DURATION = Snackbar.LENGTH_INDEFINITE;

    private enum Type {
        Default(null, null, null, null),
        Success(Color.parseColor("#2c5140"), R.drawable.baseline_check_24, Color.WHITE, "SUCCESS"),
        Failure(Color.parseColor("#8B0000"), R.drawable.baseline_cancel_24, Color.WHITE, "ERROR");

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
            if (builder.actionClickListener == null) {
                builder.actionClickListener = v -> {};
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
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

        private Builder() {}

        public Builder setActivity(Activity activity) {
            return setView(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0));
        }

        public View getView() {
            return this.view;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setText(CharSequence text) {
            this.text = text;
            return this;
        }

        public Builder setTextColor(@ColorInt int color) {
            this.textColor = color;
            return this;
        }

        public Builder setActionText(CharSequence text) {
            this.actionText = text;
            return this;
        }

        public Builder setActionTextColor(@ColorInt int color) {
            this.actionTextColor = color;
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

        public Builder setIcon(@DrawableRes int resId) {
            this.iconResId = resId;
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
                throw new IllegalStateException("You must set an Activity or a View before making a message");
            }

            if (iconResId != 0) {
                icon = ContextCompat.getDrawable(view.getContext(), iconResId);
            }

            return new Message(this).make();
        }
    }
}


