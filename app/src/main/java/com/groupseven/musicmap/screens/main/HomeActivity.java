package com.groupseven.musicmap.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.groupseven.musicmap.R;
import com.groupseven.musicmap.listeners.SessionListenerActivity;
import com.groupseven.musicmap.screens.main.artist.ArtistDataFragment;
import com.groupseven.musicmap.screens.main.feed.FeedFragment;
import com.groupseven.musicmap.screens.main.map.PostMapFragment;
import com.groupseven.musicmap.screens.main.musicmemory.create.PostFragment;
import com.groupseven.musicmap.screens.profile.ProfileActivity;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.spotify.SpotifyAccess;
import com.groupseven.musicmap.util.Constants;
import com.groupseven.musicmap.util.permissions.LocationPermission;
import com.groupseven.musicmap.util.ui.FragmentUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.groupseven.musicmap.util.ui.Message;

import java.util.concurrent.CompletableFuture;

public class HomeActivity extends SessionListenerActivity {

    private Class<? extends Fragment> lastFragmentClass = FeedFragment.class;
    private int currentLayout = R.layout.activity_home;

    private BottomNavigationView bottomNavigationView;
    private ImageView profileButton;

    private boolean hasSpotifyConnection = false;
    private boolean hasGooglePlayServices = false;

    @Override
    protected void onInternetStateChange(boolean internetAvailable) {
        if (!internetAvailable) {
            setContentView(R.layout.no_internet);
            currentLayout = R.layout.no_internet;
            return;
        }

        if (currentLayout == R.layout.activity_home) {
            return;
        }

        if (currentLayout == R.layout.no_internet) {
            setContentView(R.layout.activity_home);
            currentLayout = R.layout.activity_home;
            setupActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupActivity();

        // TODO cleanup (e.g. util method in Permission to request all)
        // only LocationPermission because others don't give popup anyway
        new LocationPermission(this).forceRequest();
    }

    @Override
    public void onSessionStateChanged() {
        super.onSessionStateChanged();
        User currentUser = Session.getInstance().getCurrentUser();
        updateNavbar(currentUser);
        setupProfileButton(currentUser);
    }

    private void setupActivity() {
        User currentUser = Session.getInstance().getCurrentUser();
        FragmentUtil.initFragment(getSupportFragmentManager(), R.id.fragment_view, lastFragmentClass);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileButton = findViewById(R.id.appbarProfile);
        updateNavbar(currentUser);
        setupProfileButton(currentUser);

        // Gray out post button as a default
        View postButton = findViewById(R.id.navbarPost);
        postButton.setAlpha(0.5F);

        checkIsPostEnabled();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // using ifs instead of switch as resource IDs will be non-final by default in
            // Android Gradle Plugin version 8.0, therefore not to be used in switch
            if (item.getItemId() == R.id.navbarFeed) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        FeedFragment.class);
                lastFragmentClass = FeedFragment.class;
                return true;
            }

            if (item.getItemId() == R.id.navbarPost) {
                if (!hasSpotifyConnection) {
                    Message.showFailureMessage(this, getString(R.string.error_spotify_not_connected));
                    return false;
                } if (!hasGooglePlayServices) {
                    Message.showFailureMessage(this, getString(R.string.error_play_services_not_connected));
                    return false;
                }

                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        PostFragment.class);
                lastFragmentClass = PostFragment.class;
                return true;
            }

            if (item.getItemId() == R.id.navbaArtistData) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        ArtistDataFragment.class);
                lastFragmentClass = ArtistDataFragment.class;
                return true;
            }

            if (item.getItemId() == R.id.navbarMap) {
                FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.fragment_view,
                        PostMapFragment.class);
                lastFragmentClass = PostMapFragment.class;
                return true;
            }

            return false;
        });
    }

    private void checkIsPostEnabled() {
        SpotifyAccess spotifyAccess = SpotifyAccess.getSpotifyAccessInstance();
        waitUserFuture().thenAcceptAsync(
                unused -> spotifyAccess.refreshToken(new SpotifyAccess.TokenCallback() {
                    @Override
                    public void onValidToken() {
                        hasSpotifyConnection = true;
                        View postButton = findViewById(R.id.navbarPost);
                        // Only enable postButton if both preconditions are true
                        if (hasGooglePlayServices) {
                            postButton.setAlpha(1);
                        }
                    }

                    @Override
                    public void onInvalidToken() {
                        hasSpotifyConnection = false;
                    }
                }), this.getMainExecutor()
        );

        hasGooglePlayServices = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
                == ConnectionResult.SUCCESS;

    }

    // TODO I NEED THIS OTHERWISE APP CRASHES
    // IS THERE A BETTER SOLUTION?
    private CompletableFuture<Void> waitUserFuture() {
        Session session = Session.getInstance();
        return CompletableFuture.supplyAsync(
                () -> {
                    while (!session.isUserLoaded()){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return null;
        });
    }

    private void updateNavbar(User currentUser) {
        if (bottomNavigationView == null) {
            return;
        }

        Menu menu = bottomNavigationView.getMenu();
        MenuItem post = menu.findItem(R.id.navbarPost);
        MenuItem artistData = menu.findItem(R.id.navbaArtistData);

        boolean isArtist = currentUser != null && currentUser.isArtist();
        post.setVisible(!isArtist);
        artistData.setVisible(isArtist);
    }

    private void setupProfileButton(User currentUser) {
        if (currentUser == null || profileButton == null) {
            return;
        }

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(Constants.PROFILE_USER_UID_ARGUMENT, currentUser.getUid());
            startActivity(intent);
            finish();
        });
    }

}