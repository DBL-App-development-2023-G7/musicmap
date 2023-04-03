package com.example.musicmap.util.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.musicmap.feed.ConcertMemory;
import com.example.musicmap.feed.MusicMemory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class Actions {

    private static final String TAG = "Actions";

    /**
     * Posts the music-memory for the {@code MusicMemory.authorUid}.
     *
     * @param musicMemory the music-memory object
     */
    public static Task<?> postMusicMemory(MusicMemory musicMemory) {
        String authorUid = musicMemory.getAuthorUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(authorUid)
                .collection("MusicMemories").add(musicMemory).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully created music memory with ID " + task.getResult().getId());
                    } else {
                        Log.e(TAG, "Could not create music memory", task.getException());
                    }
                });
    }

    /**
     * Posts the music-memory for the {@code MusicMemory.authorUid}. Overloaded method mainly used
     * for testing.
     *
     * @param firestore the firebaseFirestore reference for DI
     * @param musicMemory the music-memory object
     */
    public static Task<?> postMusicMemory(@NonNull FirebaseFirestore firestore, MusicMemory musicMemory) {
        String authorUid = musicMemory.getAuthorUid();

        return firestore.collection("Users").document(authorUid)
                .collection("MusicMemories").add(musicMemory).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully created music memory with ID " + task.getResult().getId());
                    } else {
                        Log.e(TAG, "Could not create music memory", task.getException());
                    }
                });
    }

    /**
     * Posts the concert-memory for the {@code ConcertMemory.authorUid}.
     *
     * @param concertMemory the concert-memory object
     */
    public static Task<?> postConcertMemory(ConcertMemory concertMemory) {
        String authorUid = concertMemory.getAuthorUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(authorUid)
                .collection("ConcertMemories").add(concertMemory).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully created concert memory with ID " + task.getResult().getId());
                    } else {
                        Log.e(TAG, "Could not create concert memory", task.getException());
                    }
                });
    }

    /**
     * Posts the concert-memory for the {@code ConcertMemory.authorUid}. Overloaded method mainly used
     * for testing.
     *
     * @param firestore the firebaseFirestore instance for DI
     * @param concertMemory the concert-memory object
     */
    public static Task<?> postConcertMemory(@NonNull FirebaseFirestore firestore, ConcertMemory concertMemory) {
        String authorUid = concertMemory.getAuthorUid();

        return firestore.collection("Users").document(authorUid)
                .collection("ConcertMemories").add(concertMemory).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully created concert memory with ID " + task.getResult().getId());
                    } else {
                        Log.e(TAG, "Could not create concert memory", task.getException());
                    }
                });
    }

}
