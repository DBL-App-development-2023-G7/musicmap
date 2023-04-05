package com.example.musicmap.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.musicmap.R;
import com.example.musicmap.screens.artist.ArtistDataFragment;
import com.example.musicmap.screens.map.PostMapFragment;
import com.example.musicmap.screens.profile.ProfileActivity;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.Constants;
import com.example.musicmap.util.permissions.LocationPermission;
import com.example.musicmap.util.spotify.SpotifyAuthActivity;
import com.example.musicmap.util.ui.FragmentUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends SpotifyAuthActivity {

    private Class<? extends Fragment> lastFragmentClass = FeedFragment.class;
    private int currentLayout = R.layout.activity_home;

    private BottomNavigationView bottomNavigationView;
    private ImageView profileButton;

    @Override
    protected void updateLayout(boolean internetAvailable) {
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