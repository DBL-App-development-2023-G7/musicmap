package com.example.musicmap.util.spotify;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseTokenStorage{
    private final static String REFRESH_TOKEN_FIELD = "refreshToken";
    private static String userID;

    public FirebaseTokenStorage(String userId) {
        this.userID = userId;
    }

    public interface TokenReceivedCallback {
        public void onComplete(String token);
    }

    public void getRefreshToken(TokenReceivedCallback tokenRecievedCallback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("Users").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String refreshToken = document.getString(REFRESH_TOKEN_FIELD);
                    tokenRecievedCallback.onComplete(refreshToken);
                } else {
                    Log.d("debug", "[poop] No such document");
                }
            } else {
                Log.d("debug", "[poop] Firestore failed!");
            }
        });
    }

    public void storeRefreshToken(String token) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users").document(userID)
                .update(REFRESH_TOKEN_FIELD, token)
                .addOnCompleteListener(unused ->
                        Log.d("debug", "[poop] added refreshToken")
                ).addOnFailureListener(exception ->
                        Log.d("debug", String.format("[poop] Firebase fail: %s",
                                exception.getMessage()))
                );
    }
}
