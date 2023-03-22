package com.example.musicmap.user;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.musicmap.util.firebase.AuthSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public final class Session implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "Session";

    private User currentUser;

    private static volatile Session instance;

    private ListenerRegistration userListenerRegistration;

    private Session() {
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    public static Session getInstance() {
        if (instance == null) {
            synchronized (Session.class) {
                if (instance == null) {
                    instance = new Session();
                }
            }
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            currentUser = null;
            userListenerRegistration.remove();
        } else {
            if (userListenerRegistration == null) {
                DocumentReference userDocRef = firestore.collection("Users").document(firebaseUser.getUid());
                userListenerRegistration = userDocRef.addSnapshotListener(this::refreshUserData);
            }
        }
    }

    private void refreshUserData(DocumentSnapshot doc, FirebaseFirestoreException error) {
        if (error != null) {
            Log.e(TAG, error.getMessage());
            return;
        }

        try {
            currentUser = AuthSystem.parseUserData(doc).toUser(doc.getId());
        } catch (FirebaseFirestoreException firebaseFirestoreException) {
            if (firebaseFirestoreException.getMessage() != null) {
                Log.e(TAG, firebaseFirestoreException.getMessage());
            }
        }
    }
}
