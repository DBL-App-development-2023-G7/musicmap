package com.example.musicmap.util.spotify;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

/**
 * This CLASS IS DEPRECATED
 * THIS USES THE SPOTIFY SDK LIBRARY DIRECTLY AND NOT THE WEB API
 * HOWEVER SINCE THE WEB API IS A SUPERSET OF THE SDK AND EASIER TO WORK WITH
 * I AM RIGHT NOW WORKING WITH THE WEB API WHICH IS IN SPOTIFY UTILS
 * I STILL KEEP THIS CLASS AROUND SINCE IT MAY BE USEFUL LATER
 */
public class SpotifySession implements LifecycleEventObserver{
    private SpotifyAppRemote spotifyRemoteConnection;
    private Track lastSong;
    private Activity boundActivity;


    public SpotifySession(Activity activity) {
        this.boundActivity = activity;
        Lifecycle activityLifecycle = ((LifecycleOwner) boundActivity).getLifecycle();
        activityLifecycle.addObserver(this);
    }

    public boolean isConnected() {
        return spotifyRemoteConnection.isConnected();
    }

    /**
     * Creates a spotify connection which is needed for invoking other methods of this class
     * The spotify class is destroyed if the {@code acitivty} gets destroyed as well
     */
    private void connectToSpotify() {
        ConnectionParams appAuthorizationParams =
                new ConnectionParams.Builder(SpotifyData.getClientId())
                        .setRedirectUri(SpotifyData.getRedirectUri())
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(boundActivity, appAuthorizationParams, new Connector.ConnectionListener() {
                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                    Log.d("debug", "[poop] connected!");
                    spotifyRemoteConnection = spotifyAppRemote;
                    setupPlayerListener();
                }

                public void onFailure(Throwable throwable) {
                    Log.e("MyActivity", throwable.getMessage(), throwable);
                    // Something went wrong when attempting to connect! Handle errors here
                }
        });
    }


    private void setupPlayerListener() {
        PlayerApi playerApi = spotifyRemoteConnection.getPlayerApi();
        playerApi.getPlayerState()
                .setResultCallback(playerState -> {
                    if(!playerState.track.isPodcast && !playerState.track.isEpisode) {
                        Log.d("debug", "[poop] Initial track update!");
                        lastSong = playerState.track;
                    }}
                );
        playerApi.subscribeToPlayerState()
                .setEventCallback(
                        new Subscription.EventCallback<PlayerState>() {
                            @Override
                            public void onEvent(PlayerState data) {
                                Log.d("debug", "[poop] Track update!");
                                if(!data.track.isPodcast && !data.track.isEpisode) {
                                    lastSong = data.track;
                                }
                            }
                        }
                );
    }

    /**
     * Returns the most recent spotify song
     * @return the most recent song (could be null!)
     */
    public Track getLastSong() {
        return lastSong;
    }


    /**
     * A util method to load an SOn image into a view
     * @param song the song with the image
     * @param view the view to load the image into
     */
    public void loadSongImageIntoView(Track song, ImageView view) {
        spotifyRemoteConnection
                .getImagesApi()
                .getImage(song.imageUri)
                .setResultCallback(
                         bitmap -> {
                             Log.d("debug",String.format("Thread %d",Thread.currentThread().getId()));
                             view.setImageBitmap(bitmap);
                         }
                );
    }

    /**
     * A method of the LifecycleEventObserver
     * Used internally to get updates on the calling activity
     * @param source The source of the event
     * @param event The event
     */
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event){
        if (event == Lifecycle.Event.ON_START) {
            Log.d("debug", "[poop] Post view created!");
            connectToSpotify();
        }
        if (event == Lifecycle.Event.ON_STOP) {
            // connection could be null bu the disconnect method does not care
            Log.d("debug", "[poop] Post view destroyed!");
            SpotifyAppRemote.disconnect(spotifyRemoteConnection);
        }
    }

}
