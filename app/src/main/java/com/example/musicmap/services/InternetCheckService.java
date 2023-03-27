package com.example.musicmap.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicmap.R;
import com.example.musicmap.util.Constants;

/**
 * A service that runs in the background to check internet connection every fixed interval (5 seconds).
 * If internet connection is not available, it displays a Toast message.
 */
public class InternetCheckService extends Service {

    private static final String TAG = "InternetCheckService";
    private static final int INTERVAL = 5000; // 5 seconds interval

    private Handler handler;
    private Runnable runnable;
    private boolean isRunning = false;

    /**
     * Called when the service is created.
     * Initializes the handler and the runnable to check for internet connection.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Context context = getApplicationContext();

                    if (!isInternetAvailable(context)) {
                        Log.w(TAG, "No internet connection found");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            // TODO: refactor notification permission to work for Android 13+
                            Toast.makeText(context, getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                        } else {
                            showInternetConnectionUnavailableNotification();
                        }
                    }
                } finally {
                    handler.postDelayed(this, INTERVAL);
                }
            }
        };
    }

    /**
     * Called when the service is started.
     * Starts the handler to check for internet connection at the specified interval.
     *
     * @param intent  The Intent that started this service.
     * @param flags   Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value indicates what semantics the system should use for the service
     * if it is killed while running.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            handler.postDelayed(runnable, INTERVAL);
        }
        return START_STICKY;
    }

    /**
     * Called when the service is bound to an activity.
     * This service does not support binding, so this method returns null.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return null, since this service does not support binding.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Checks if internet connection is available.
     *
     * @param context The context to use for getting the system service.
     * @return true if internet connection is available, false otherwise.
     */
    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Shows a notification for no internet connection error.
     * It creates a notification using the notification channel for internet check service.
     */
    private void showInternetConnectionUnavailableNotification() {
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this, Constants.INTERNET_CHECK_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.baseline_cancel_24)
                .setContentTitle(getString(R.string.error_no_internet_title))
                .setContentText(getString(R.string.error_no_internet))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Constants.INTERNET_CHECK_NOTIFICATION_ID, builder.build());
    }

}
