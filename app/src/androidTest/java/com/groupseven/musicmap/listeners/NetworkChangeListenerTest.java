package com.groupseven.musicmap.listeners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.os.Looper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NetworkChangeListenerTest {

    @Mock
    private ConnectivityManager mockConnectivityManager;

    @Mock
    private Network mockNetwork;

    @Mock
    private Context mockContext;

    private NetworkChangeListener networkChangeListener;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Handler handler = new Handler(Looper.getMainLooper());
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        doNothing().when(mockContext).sendBroadcast(any());
        networkChangeListener = new NetworkChangeListener(mockContext, handler);
    }

    @Test
    public void testInternetAvailable() {
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        networkChangeListener.onAvailable(mockNetwork);

        assertTrue(networkChangeListener.isConnected());
    }

    @Test
    public void testInternetUnavailable() {
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(null);
        networkChangeListener.onLost(mockNetwork);

        assertFalse(networkChangeListener.isConnected());
    }

}
