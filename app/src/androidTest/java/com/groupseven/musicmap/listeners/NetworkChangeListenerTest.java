package com.groupseven.musicmap.listeners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.net.Network;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NetworkChangeListenerTest {

    @Mock
    private Context context;

    @Mock
    private Network mockNetwork;

    private NetworkChangeListener networkChangeListener;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mockNetwork = mock(Network.class);
        networkChangeListener = new NetworkChangeListener(context);
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
        assertFalse(networkChangeListener.isConnected());

        networkChangeListener.onAvailable(mockNetwork);
        assertTrue(networkChangeListener.isConnected());

        networkChangeListener.onLost(mockNetwork);
        assertFalse(networkChangeListener.isConnected());
    }

}

