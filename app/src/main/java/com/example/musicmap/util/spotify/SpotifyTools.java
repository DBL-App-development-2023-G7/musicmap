package com.example.musicmap.util.spotify;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.function.Function;

public class SpotifyTools implements LifecycleEventObserver{
    private static SpotifyTools instance;
    private static final String CLIENT_ID = "56ab7fed83514a7a96a7b735737280d8";
    private static final String REDIRECT_URI = "musicmap://spotify-auth";
    private SpotifyAppRemote spotifyRemoteConnection;
    private Track lastSong;
    private SpotifyTools() {
    }

    public static SpotifyTools getInstance() {
        if (instance == null) {
            return new SpotifyTools();
        }
        return instance;
    }

    /**
     * Creates a spotify connection which is needed for invoking other methods of this class
     * The spotify class is destroyed if the {@code acitivty} gets destroyed as well
     * @param activity the activity which request a spotify connection
     */
    public void connectToSpotify(Activity activity) {
        Lifecycle activityLifecycle = ((LifecycleOwner) activity).getLifecycle();
        activityLifecycle.addObserver(this);

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(activity, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        spotifyRemoteConnection = spotifyAppRemote;
                        setupPlayerListener();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
            });
    }

    public void checkConnention(){
        if(spotifyRemoteConnection == null) {
            Log.d("debug", "[poop] No connection ;(");
        } else {
            Log.d("debug", "[poop] POGGERS THJERE IS ACOADFJSOQJK");
        }
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

    public void playTestPlayList() {
        // TODO add exceptions if not connected
        // Play a playlist
        spotifyRemoteConnection.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        spotifyRemoteConnection.getPlayerApi()
            .subscribeToPlayerState()
            .setEventCallback(playerState -> {
                final Track track = playerState.track;
                if (track != null) {
                    Log.d("MainActivity", track.name + " by " + track.artist.name);
                }
        });
    }

    /**
     * A method of the LifecycleEventObserver
     * Used internally to get updates on the calling activity
     * @param source The source of the event
     * @param event The event
     */
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event){
        if (event == Lifecycle.Event.ON_DESTROY) {
            // connection could be null bu the disconnect method does not care
            Log.d("debug", "[poop] Post view destroyed!");
            SpotifyAppRemote.disconnect(spotifyRemoteConnection);
        }
    }
}
