package com.example.musicmap.screens.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmap.R;
import com.example.musicmap.screens.HomeActivity;
import com.example.musicmap.util.ui.FragmentUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AuthActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final int FRAGMENT_CONTAINER_ID = R.id.fragment_container_view;

    private static final String TAG = "FirebaseAuth";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);

        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (savedInstanceState == null) {
            FragmentUtil.initFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                    LoginFragment.class);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
        user = auth.getCurrentUser();

        if (user != null) {
            loadActivityBasedOnVerificationStatus();
        } else {
            FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                    LoginFragment.class);
        }
    }

    public void loadLoginFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                LoginFragment.class);
    }

    public void loadRegisterFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                RegisterFragment.class);
    }

    public void loadRegisterArtistFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                RegisterArtistFragment.class);
    }

    private void loadVerificationFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), FRAGMENT_CONTAINER_ID,
                VerificationFragment.class);
    }

    private void loadHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(homeIntent);
        Log.d(TAG, "Started Home Activity");
        finish();
    }

    public void loadActivityBasedOnVerificationStatus() {
        firestore.collection("Users").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    // TODO separate frontend (Activity) from Firebase interaction
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "firestore:fail");
                        return;
                    }

                    DocumentSnapshot doc = task.getResult();
                    if (!doc.exists()) {
                        Log.d(TAG, "findDoc:fail");
                        return;
                    }

                    Map<String, Object> data = doc.getData();
                    if (data == null) {
                        Log.d(TAG, "data:fail");
                        return;
                    }

                    Object artistFirebaseBoolean = data.get("artist");
                    if (!(artistFirebaseBoolean instanceof Boolean)) {
                        Log.e(TAG, "userStructure:mismatch");
                        return;
                    }

                    boolean isArtist = (boolean) artistFirebaseBoolean;

                    if (isArtist) {
                        Log.d(TAG, "User is an artist");

                        Object verifiedFirebaseBoolean = data.get("verified");
                        if (!(verifiedFirebaseBoolean instanceof Boolean)) {
                            Log.e(TAG, "userStructure:mismatch");
                            return;
                        }

                        boolean verified = (boolean) verifiedFirebaseBoolean;

                        if (!verified) {
                            Log.d(TAG, "Artist is not verified");
                            Log.d(TAG, "Loading the verification fragment");
                            loadVerificationFragment();
                            return;
                        }
                        Log.d(TAG, "Artist is verified");
                    }

                    Log.d(TAG, "Loading the home activity");
                    loadHomeActivity();
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

}