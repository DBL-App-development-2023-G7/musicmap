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
    private static final String TAG = "FirebaseAuth";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    private static final int fragmentContainerID = R.id.fragment_container_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
        user = auth.getCurrentUser();

        if (user != null) {
            loadActivityBasedOnVerificationStatus();
        } else {
            FragmentUtil.initFragment(getSupportFragmentManager(), fragmentContainerID,
                    LoginFragment.class);
        }
    }

    public void loadLoginFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), fragmentContainerID,
                LoginFragment.class);
    }

    public void loadRegisterFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), fragmentContainerID,
                RegisterFragment.class);
    }

    public void loadRegisterArtistFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), fragmentContainerID,
                RegisterArtistFragment.class);
    }

    private void loadVerificationFragment() {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), fragmentContainerID,
                VerificationFragment.class);
    }

    private void loadHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(homeIntent);
        finish();
    }

    public void loadActivityBasedOnVerificationStatus() {
        user = auth.getCurrentUser();
        firestore.collection("Users").document(user.getUid()).get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Log.d(TAG, "firestore:fail");
                        return;
                    }
                    Log.d(TAG, "firestore:success");

                    DocumentSnapshot doc = task.getResult();

                    if (!doc.exists()) {
                        Log.d(TAG, "findDoc:fail");
                        return;
                    }
                    Log.d(TAG, "findDoc:success");

                    Map<String, Object> data = doc.getData();
                    if (data == null) {
                        Log.d(TAG, "data:fail");
                        return;
                    }
                    Log.d(TAG, "data:success");

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
                    } else {
                        Log.d(TAG, "Loading the home activity");
                        loadHomeActivity();
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }
}