package com.groupseven.musicmap.util.network;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.groupseven.musicmap.util.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NetworkUtilsTest {

    @Mock
    private ConnectivityManager mockConnectivityManager;

    private Context mockContext;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
    }

    @Test
    public void testIsInternetConnectionAvailable_returnsFalse() {
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(null);

        assertFalse(NetworkUtils.isInternetConnectionAvailable(mockContext));
    }

    @Test
    public void testSendInternetBroadcast() {
        boolean isConnected = true;
        Intent expectedIntent = new Intent(Constants.INTERNET_BROADCAST_ACTION);
        expectedIntent.putExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, isConnected);

        NetworkUtils.sendInternetBroadcast(mockContext, isConnected);

        verify(mockContext).sendBroadcast(argThat(intent ->
                intent.getAction().equals(expectedIntent.getAction())
                        && intent.getBooleanExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, false)
                        == expectedIntent.getBooleanExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, false)));
    }

}


