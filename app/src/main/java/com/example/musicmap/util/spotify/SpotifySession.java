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
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class SpotifySession implements LifecycleEventObserver{
    private SpotifyAppRemote spotifyRemoteConnection;
    private Track lastSong;
    private Activity boundActivity;


    public SpotifySession(Activity activity) {
        this.boundActivity = activity;
        Lifecycle activityLifecycle = ((LifecycleOwner) boundActivity).getLifecycle();
        activityLifecycle.addObserver(this);
    }

    public void setToken(){

    }

    public boolean isConnected() {
        return spotifyRemoteConnection.isConnected();
    }
    public void checkConnection(){
        if(spotifyRemoteConnection == null) {
            Log.d("debug", "[poop] No connection ;(");
        } else {
            Log.d("debug", "[poop] POGGERS THJERE IS ACOADFJSOQJK");
        }
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

    public Track getLastSong() {
        return lastSong;
    }


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

    public void debugTrack(Track track){
        Log.d("debug",String.format("[poop] name: %s", track.name));
        Log.d("debug",String.format("[poop] album: %s", track.album.name));
        Log.d("debug",String.format("[poop] artist: %s", track.artist.name));
        Log.d("debug",String.format("[poop] image uri: %s", track.imageUri.raw));
        Log.d("debug",String.format("[poop] uri: %s", track.uri.toString()));
        Log.d("debug",String.format("[poop] isPodcast: %b", track.isPodcast));
        Log.d("debug",String.format("[poop] isEpisode: %b", track.isEpisode));
    }

}
