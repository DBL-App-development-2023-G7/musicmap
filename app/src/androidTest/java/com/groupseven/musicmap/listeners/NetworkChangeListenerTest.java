package com.groupseven.musicmap.listeners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;

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
        doAnswer(invocation -> {
            networkChangeListener.onAvailable(mockNetwork);
            return null;
        }).when(mockNetwork);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            assertTrue(networkChangeListener.isConnected());
        }, 1000);
    }

    @Test
    public void testInternetUnavailable() {
        doAnswer(invocation -> {
            networkChangeListener.onLost(mockNetwork);
            return null;
        }).when(mockNetwork);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            assertFalse(networkChangeListener.isConnected());
        }, 1000);
    }

}

