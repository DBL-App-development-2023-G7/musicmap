package com.groupseven.musicmap.util.firebase;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.util.Constants;

/**
 * This class is responsible for handling the storage and retrieval of Spotify refresh tokens in the
 * Firebase Firestore database for a specific {@link User#getUid()}.
 */
public class SpotifyTokenStorage {

    private static final String TAG = "FirebaseTokenStorage";

    /**
     * The user id of the user to associate with this {@code SpotifyTokenStorage} instance;
     */
    private final String userID;

    /**
     * Constructor for the class with the specified user ID.
     *
     * @param userId the ID of the user for which the token storage instance will be created
     */
    public SpotifyTokenStorage(String userId) {
        this.userID = userId;
    }

    /**
     * Retrieves the Spotify refresh token for the user associated with this {@code SpotifyTokenStorage}
     * instance from the Firebase Firestore database.
     *
     * @param tokenReceivedCallback a callback to receive the retrieved Spotify refresh token
     */
    public void getRefreshToken(TokenReceivedCallback tokenReceivedCallback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("Users").document(userID);

        docRef.get().addOnSuccessListener(document -> {

            if (!document.exists()) {
                Log.d(TAG, "The user does not have a refresh token stored in the Firestore database.");
                return;
            }

            String refreshToken = document.getString(Constants.SPOTIFY_REFRESH_TOKEN_FIELD);
            tokenReceivedCallback.onComplete(refreshToken);
        }).addOnFailureListener(exception ->
                Log.d(TAG, String.format("Firestore failed while trying to get the Spotify refresh token. %s",
                        exception.getMessage())));
    }

    /**
     * Stores the specified Spotify refresh token in the Firebase Firestore database for the user
     * associated with this {@code SpotifyTokenStorage} instance.
     *
     * @param token the Spotify refresh token to store
     */
    public void storeRefreshToken(String token) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users").document(userID)
                .update(Constants.SPOTIFY_REFRESH_TOKEN_FIELD, token)
                .addOnSuccessListener(unused ->
                        Log.d(TAG, "Added refreshToken to the Firebase Firestore database."))
                .addOnFailureListener(exception ->
                        Log.d(TAG, String.format("Firebase fail: %s", exception.getMessage()))
        );
    }

    /**
     * An interface for receiving the Spotify refresh token retrieved from the Firebase Firestore database.
     */
    public interface TokenReceivedCallback {

        /**
         * Called when the Spotify refresh token has been successfully retrieved from the Firebase
         * Firestore database.
         *
         * @param token the retrieved Spotify refresh token
         */
        void onComplete(String token);
    }

}
