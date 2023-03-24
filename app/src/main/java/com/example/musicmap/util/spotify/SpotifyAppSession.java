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
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyAppSession implements LifecycleEventObserver{
    private static final String CLIENT_ID = "56ab7fed83514a7a96a7b735737280d8";
    private static final String REDIRECT_URI = "musicmap://spotify-auth";
    private SpotifyAppRemote spotifyRemoteConnection;
    private Track lastSong;
    private Activity boundActivity;

    public SpotifyAppSession(Activity activity) {
        this.boundActivity = activity;
        Lifecycle activityLifecycle = ((LifecycleOwner) boundActivity).getLifecycle();
        activityLifecycle.addObserver(this);
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
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
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
        String imageUriString = song.imageUri.raw;
        // imageUri is of the form spotify:image:dhgskjglskagdsahgdjksakgj
        Log.d("debug", String.format("[poop] URI %s", imageUriString));

        // extract the last part
        Pattern pattern = Pattern.compile("spotify:image:(.+)");
        Matcher matcher = pattern.matcher(imageUriString);
        if (!matcher.matches()) {
            throw new IllegalStateException("Received Spotify Image URI does not match excepted pattern");
        }

        String id = matcher.group(1);

        Log.d("debug", String.format("[poop] id %s", id));

        // append the image id to the download url
        String final_url = "https://i.scdn.co/image/" + id;

        Picasso.get().load(final_url).into(view);
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
