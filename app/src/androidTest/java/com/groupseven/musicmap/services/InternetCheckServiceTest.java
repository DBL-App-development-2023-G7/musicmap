package com.groupseven.musicmap.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import org.junit.Before;
import org.junit.Test;

public class InternetCheckServiceTest {

    private InternetCheckService service;
    private Context context;

    @Before
    public void setUp() {
        service = new InternetCheckService();
        context = mock(Context.class);
    }

    @Test
    public void testIsInternetAvailableWithNoNetwork() {
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(null);

        assertFalse(service.isInternetAvailable(context));
    }

    @Test
    public void testIsInternetAvailableWithDisconnectedNetwork() {
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.isConnected()).thenReturn(false);

        assertFalse(service.isInternetAvailable(context));
    }

    @Test
    public void testIsInternetAvailableWithConnectedNetwork() {
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.isConnected()).thenReturn(true);

        assertTrue(service.isInternetAvailable(context));
    }

    @Test
    public void testOnBind() {
        Intent intent = mock(Intent.class);
        IBinder binder = service.onBind(intent);
        assertNull(binder);
    }

}

