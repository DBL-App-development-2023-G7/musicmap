package com.example.musicmap.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicmap.MusicMap;
import com.example.musicmap.R;
import com.example.musicmap.util.ui.Message;

public class InternetCheckService extends Service {
    private static final String TAG = "InternetCheckService";
    private Handler handler;
    private Runnable runnable;
    private static final int INTERVAL = 5000; // 5 seconds interval
    private boolean isRunning = false;

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
                        Toast.makeText(context, getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }
                } finally {
                    handler.postDelayed(this, INTERVAL);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            handler.postDelayed(runnable, INTERVAL);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

}
