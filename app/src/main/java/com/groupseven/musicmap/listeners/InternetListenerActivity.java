package com.groupseven.musicmap.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.groupseven.musicmap.util.Constants;

/**
 * An abstract base class for activities that is extended by {@link SessionListenerActivity}.
 *
 * The class provides an implementation that listens for changes in internet availability by registering
 * a BroadcastReceiver that listens for the action Constants.INTERNET_BROADCAST_ACTION.
 * Whenever this BroadcastReceiver is triggered, it calls the abstract method {@link #updateLayout(boolean)}
 * that must be implemented by the child activities. This method is responsible for updating the layout
 * to reflect the current internet availability status.
 */
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

    /**
     * Called when the activity is created.
     * Registers the BroadcastReceiver to listen for changes in internet availability.
     * @param savedInstanceState the saved instance state.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter(Constants.INTERNET_BROADCAST_ACTION);
        registerReceiver(internetCheckReceiver, intentFilter);
    }

    /**
     * Called when the activity is destroyed.
     * Unregisters the BroadcastReceiver.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetCheckReceiver);
    }
}
