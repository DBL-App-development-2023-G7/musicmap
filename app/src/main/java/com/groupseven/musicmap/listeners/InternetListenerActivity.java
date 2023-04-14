package com.groupseven.musicmap.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.groupseven.musicmap.util.Constants;

public abstract class InternetListenerActivity extends AppCompatActivity {

    private final BroadcastReceiver internetCheckReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTERNET_BROADCAST_ACTION)) {
                boolean internetAvailable = intent.getBooleanExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, true);
                updateLayout(internetAvailable);
            }
        }
    };

    /**
     * Abstract method that the child activities must override, to dynamically switch the layout
     * from the activity/fragment to layout for no internet.
     *
     * @param internetAvailable true if internet connection available, false otherwise.
     */
    protected abstract void updateLayout(boolean internetAvailable);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter(Constants.INTERNET_BROADCAST_ACTION);
        registerReceiver(internetCheckReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetCheckReceiver);
    }
}
