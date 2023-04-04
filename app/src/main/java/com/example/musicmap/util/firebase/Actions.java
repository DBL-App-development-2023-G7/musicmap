package com.example.musicmap.util.firebase;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.musicmap.feed.ConcertMemory;
import com.example.musicmap.feed.MusicMemory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

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

    /**
     * Uploads music memory image for the user.
     *
     * @param capturedImage the bitmap of the captured image
     * @param authorID the id of the author
     */
    public static Task<?> uploadMusicMemoryImage(Bitmap capturedImage, String authorID) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        StorageReference rootReference = FirebaseStorage.getInstance().getReference();
        String uuid = UUID.randomUUID().toString();
        StorageReference imageRef = rootReference.child(String.format("users/%s/memories/%s.jpg", authorID, uuid));
        UploadTask uploadTask = imageRef.putBytes(data);

        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Could not upload the image", task.getException());
            }

            return imageRef.getDownloadUrl();
        });
    }

}
