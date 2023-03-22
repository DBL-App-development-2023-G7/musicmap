package com.example.musicmap.util;

import android.util.Log;

import com.example.musicmap.feed.ConcertMemory;
import com.example.musicmap.feed.MusicMemory;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MemoryStorage {

    private static final String TAG = "MemoryStorage";

    public static Task<?> postMusicMemory(MusicMemory musicMemory) {
        String authorUid = musicMemory.getAuthorUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(authorUid)
                .collection("MusicMemories").add(musicMemory).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully created music memory with ID " + task.getResult().getId());
                    } else {
                        Log.w(TAG, "Could not create music memory");
                    }
                });
    }

    public static Task<?> postConcertMemory(ConcertMemory concertMemory) {
        String authorUid = concertMemory.getAuthorUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(authorUid)
                .collection("ConcertMemories").add(concertMemory).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully created concert memory with ID " + task.getResult().getId());
                    } else {
                        Log.w(TAG, "Could not create concert memory");
                    }
                });
    }

    public static Task<MusicMemory> getMusicMemory(String authorUid, String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(authorUid)
                .collection("MusicMemories").document(uid).get().continueWithTask(task -> {
                    TaskCompletionSource<MusicMemory> taskCompletionSource = new TaskCompletionSource<>();

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        MusicMemory musicMemory = document.toObject(MusicMemory.class);

                        if (musicMemory == null) {
                            taskCompletionSource.setException(new NullPointerException(
                                    "Music memory (" + authorUid + ", " + uid + ")"));

                            return taskCompletionSource.getTask();
                        }

                        String authorId =
                                Objects.requireNonNull(document.getReference().getParent().getParent()).getId();
                        musicMemory.setAuthorUid(authorId);

                        taskCompletionSource.setResult(musicMemory);
                    } else {
                        assert task.getException() != null;
                        taskCompletionSource.setException(task.getException());

                    }

                    return taskCompletionSource.getTask();
                });
    }

    public static Task<List<MusicMemory>> getMusicMemories(String authorUid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(authorUid)
                .collection("MusicMemories").get().continueWithTask(task -> {
                    TaskCompletionSource<List<MusicMemory>> taskCompletionSource = new TaskCompletionSource<>();

                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        List<MusicMemory> musicMemories = new ArrayList<>();

                        for (DocumentSnapshot document : documents) {
                            MusicMemory musicMemory = document.toObject(MusicMemory.class);

                            if (musicMemory == null) {
                                continue;
                            }

                            String authorId =
                                    Objects.requireNonNull(document.getReference().getParent().getParent()).getId();
                            musicMemory.setAuthorUid(authorId);

                            musicMemories.add(musicMemory);
                        }

                        taskCompletionSource.setResult(musicMemories);
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                    return taskCompletionSource.getTask();
                });
    }

}
