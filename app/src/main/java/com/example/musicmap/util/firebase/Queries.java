package com.example.musicmap.util.firebase;

import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.feed.Post;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class Queries {

    /**
     * Fetches all the user(s) by username.
     *
     * @param username the username of the user
     * @return the user
     */
    public static Task<QuerySnapshot> getUsersWithUsername(String username) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("Users").whereEqualTo("username", username);
        return query.get();
    }

    /**
     * Fetches the user by email.
     *
     * @param email the email of the user
     * @return the user
     */
    public static Task<QuerySnapshot> getUserWithEmail(String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("Users").whereEqualTo("email", email);
        return query.get();
    }

    /**
     * Fetches the music memory for an author by id.
     * Use only for the current user.
     *
     * @param authorUid the id of the author
     * @param uid the id of the music-memory
     * @return music-memory
     */
    public static Task<MusicMemory> getMusicMemoryByAuthorIdAndId(String authorUid, String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(authorUid)
                .collection("MusicMemories").document(uid).get().continueWithTask(task -> {
                    TaskCompletionSource<MusicMemory> taskCompletionSource = new TaskCompletionSource<>();

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        MusicMemory musicMemory = deserialize(document, MusicMemory.class);

                        taskCompletionSource.setResult(musicMemory);
                    } else {
                        assert task.getException() != null;
                        taskCompletionSource.setException(task.getException());

                    }

                    return taskCompletionSource.getTask();
                });
    }

    /**
     * Fetches all the music memories for an author.
     * Use only for the current user.
     *
     * @param authorUid the id of the author
     * @return all music-memories for the author
     */
    public static Task<List<MusicMemory>> getMusicMemoriesByAuthorId(String authorUid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(authorUid)
                .collection("MusicMemories").get().continueWithTask(task -> {
                    TaskCompletionSource<List<MusicMemory>> taskCompletionSource = new TaskCompletionSource<>();

                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        List<MusicMemory> musicMemories = documents.stream()
                                .map(document -> deserialize(document, MusicMemory.class))
                                .collect(Collectors.toList());

                        taskCompletionSource.setResult(musicMemories);
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                    return taskCompletionSource.getTask();
                });
    }

    /**
     * Fetches all the music memories (used for the feed).
     *
     * @return feed
     */
    public static Task<List<MusicMemory>> getAllMusicMemories() {
        // TODO: update the implementation based on how we decide to limit the feed
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        return firestore.collectionGroup("MusicMemories").get().continueWithTask(task -> {
            TaskCompletionSource<List<MusicMemory>> taskCompletionSource = new TaskCompletionSource<>();

            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                List<MusicMemory> musicMemories = documents.stream()
                        .map(document -> deserialize(document, MusicMemory.class))
                        .collect(Collectors.toList());

                taskCompletionSource.setResult(musicMemories);
            } else {
                taskCompletionSource.setException(task.getException());
            }
            return taskCompletionSource.getTask();
        });
    }

    /**
     * Deserializes the given document into a post of the given class.
     *
     * @param document the document snapshot.
     * @param postClass the class of the post, e.g. {@code MusicMemory.class} or {@code ConcertMemory.class}.
     * @return the deserialized post, or {@code null}
     * @param <P> the type of post, e.g. {@link MusicMemory}.
     */
    private static <P extends Post> P deserialize(DocumentSnapshot document, Class<P> postClass) {
        if (document == null) {
            throw new NullPointerException("document");
        }
        if (postClass == null) {
            throw new NullPointerException("postClass");
        }

        P post = document.toObject(postClass);

        if (post == null) {
            throw new IllegalArgumentException("The DocumentSnapshot contained a null document");
        }

        DocumentReference authorDocumentReference = document.getReference().getParent().getParent();
        if (authorDocumentReference == null) {
            throw new IllegalStateException("The given document doesn't have an author");
        }

        String authorId = authorDocumentReference.getId();
        post.setAuthorUid(authorId);

        return post;
    }
}
