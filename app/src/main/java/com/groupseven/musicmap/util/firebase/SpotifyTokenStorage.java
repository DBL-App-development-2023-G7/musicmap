package com.groupseven.musicmap.util.firebase;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SpotifyTokenStorage {

    private static final String TAG = "FirebaseTokenStorage";
    private static final String REFRESH_TOKEN_FIELD = "refreshToken";

    private final String userID;

    public SpotifyTokenStorage(String userId) {
        this.userID = userId;
    }

    public interface TokenReceivedCallback {
        void onComplete(String token);
    }

    public void getRefreshToken(TokenReceivedCallback tokenReceivedCallback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("Users").document(userID);

        docRef.get().addOnSuccessListener(document -> {

            if (!document.exists()) {
                Log.d(TAG, "The user does not have a refresh token stored in the Firestore database.");
                return;
            }

            String refreshToken = document.getString(REFRESH_TOKEN_FIELD);
            tokenReceivedCallback.onComplete(refreshToken);
        }).addOnFailureListener(exception ->
                Log.d(TAG, String.format("Firestore failed while trying to get the Spotify refresh token. %s",
                        exception.getMessage())));
    }

    public void storeRefreshToken(String token) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users").document(userID)
                .update(REFRESH_TOKEN_FIELD, token)
                .addOnSuccessListener(unused ->
                        Log.d(TAG, "Added refreshToken to the Firebase Firestore database."))
                .addOnFailureListener(exception ->
                        Log.d(TAG, String.format("Firebase fail: %s", exception.getMessage()))
        );
    }

}
