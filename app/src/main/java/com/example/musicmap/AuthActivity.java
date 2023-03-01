package com.example.musicmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.musicmap.util.ui.FragmentUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        if (user != null) {
            loadNext();
        } else if (savedInstanceState == null) {
            FragmentUtil.initFragment(getSupportFragmentManager(), fragmentContainerID,
                    LoginFragment.class);
        }
    }

    //TODO
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    public void onStart() {
        super.onStart();
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

    //TODO: rename this function & decompose it
    public void loadNext() {
        user = auth.getCurrentUser();
        firestore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firestore:success");
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d(TAG, "findDoc:success");
                        Map<String, Object> data = doc.getData();
                        boolean artist = (boolean) data.get("artist");

                        if (artist) {
                            boolean verified = (boolean) data.get("verified");
                            Log.d(TAG, "User is an artist");
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
                    } else {
                        Log.d(TAG, "findDoc:fail");
                    }
                } else {
                    Log.d(TAG, "firestore:fail");
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }
}