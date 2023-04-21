package com.groupseven.musicmap.listeners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.Network;
import android.os.Handler;
import android.os.Looper;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NetworkChangeListenerTest {

    @Mock
    private Network mockNetwork;

    private NetworkChangeListener networkChangeListener;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Handler handler = new Handler(Looper.getMainLooper());
        networkChangeListener = new NetworkChangeListener(context, handler);
    }

    @Test
    public void testInternetAvailable() {
        networkChangeListener.onAvailable(mockNetwork);
        assertTrue(networkChangeListener.isConnected());
    }

    @Test
    public void testInternetUnavailable() {
        networkChangeListener.onLost(mockNetwork);
        assertFalse(networkChangeListener.isConnected());
    }

    @Test
    public void testIsConnected() {
        networkChangeListener.onAvailable(mockNetwork);
        assertTrue(networkChangeListener.isConnected());

        networkChangeListener.onLost(mockNetwork);
        assertFalse(networkChangeListener.isConnected());
    }

}

