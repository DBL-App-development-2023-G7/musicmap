package com.groupseven.musicmap.util.firebase;

import com.groupseven.musicmap.models.ArtistData;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.models.Post;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.models.SongCount;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.models.UserData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.groupseven.musicmap.util.TaskUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Queries {

    /**
     * Fetches the user with a given username.
     *
     * @param username the username of the user
     * @return the future containing the user, or {@code null} if the user doesn't exist.
     */
    public static CompletableFuture<User> getUserWithUsername(String username) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return TaskUtil.getFuture(firestore.collection("Users")
                .whereEqualTo("username", username)
                .get())
                .thenApply(query -> {
                    // No users found
                    if (query.isEmpty()) {
                        return null;
                    }

                    // More than one user found
                    if (query.size() > 1) {
                        throw new IllegalStateException(
                                "More than two user with username '" + username + "' exist");
                    }

                    DocumentSnapshot doc = query.getDocuments().get(0);

                    // Deserialize user
                    return deserializeUser(doc);
                });
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
                        if (task.getException() == null) {
                            taskCompletionSource.setException(new RuntimeException("Could not fetch music memories"));
                        } else {
                            taskCompletionSource.setException(task.getException());
                        }
                    }
                    return taskCompletionSource.getTask();
                });
    }

    /**
     * Fetches all the music memories created in the last 24 hours.
     *
     * @return music memories in last 24 hours.
     */
    public static Task<List<MusicMemory>> getAllMusicMemoriesInLastTwentyFourHours() {
        long timestamp24HoursAgo = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24);

        return getAllMusicMemories(Filter.greaterThan("timePosted", new Date(timestamp24HoursAgo)));
    }

    /**
     * Fetches all the music memories.
     *
     * @return all music memories
     */
    public static Task<List<MusicMemory>> getAllMusicMemories() {
        return getAllMusicMemories(Filter.or());
    }

    /**
     * Fetches all the music memories made with songs of a certain Spotify artist.
     *
     * @param spotifyArtistId the id of the Spotify artist.
     * @return all music memories with songs of the given artist.
     */
    public static Task<List<MusicMemory>> getAllMusicMemoriesWithSpotifyArtistId(String spotifyArtistId) {
        return getAllMusicMemories(Filter.equalTo("song.spotifyArtistId", spotifyArtistId));
    }

    /**
     * Fetches the most popular songs for an artist.
     *
     * @param artistId the id of the artist
     * @param count the number of songs to return (or all, whichever less)
     * @return {@code count} number of most popular songs for the artist
     */
    public static Task<List<SongCount>> getMostPopularSongsByArtist(String artistId, int count) {
        return getAllMusicMemoriesWithSpotifyArtistId(artistId)
                .continueWith(task -> {
                    Map<Song, Long> songMap = new HashMap<>();

                    for (MusicMemory musicMemory : task.getResult()) {
                        Song song = musicMemory.getSong();

                        if (song != null) {
                            songMap.put(song, songMap.getOrDefault(song, 0l) + 1);
                        }
                    }

                    return songMap.entrySet().stream()
                            .sorted(Map.Entry.<Song, Long>comparingByValue().reversed())
                            .limit(count)
                            .map(entry -> new SongCount(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
                });
    }

    /**
     * Fetches all the music memories matching a given filter.
     *
     * @param filter the filter to use for matching.
     * @return all music memories matching the given.
     */
    private static Task<List<MusicMemory>> getAllMusicMemories(Filter filter) {
        // TODO: update the implementation based on how we decide to limit the feed
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        //CSOFF: Indentation
        return firestore.collectionGroup("MusicMemories")
                .where(filter)
                .get()
                .continueWithTask(task -> {
            TaskCompletionSource<List<MusicMemory>> taskCompletionSource = new TaskCompletionSource<>();

            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                List<MusicMemory> musicMemories = documents.stream()
                        .map(document -> deserialize(document, MusicMemory.class))
                        .collect(Collectors.toList());

                taskCompletionSource.setResult(musicMemories);
            } else {
                if (task.getException() == null) {
                    taskCompletionSource.setException(new RuntimeException("Could not fetch music memories"));
                } else {
                    taskCompletionSource.setException(task.getException());
                }
            }

            return taskCompletionSource.getTask();
        });
        //CSON: Indentation
    }

    /**
     * Deserializes the given document into a post of the given class.
     *
     * @param document the document snapshot.
     * @param postClass the class of the post, e.g. {@code MusicMemory.class}.
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

    /**
     * Deserializes a document snapshot containing user data.
     *
     * @param doc the document snapshot.
     * @return the user.
     */
    private static User deserializeUser(DocumentSnapshot doc) throws IllegalStateException {
        if (!doc.exists()) {
            throw new IllegalStateException("Document does not exist");
        }

        String uid = doc.getReference().getId();
        UserData userData = doc.toObject(UserData.class);

        if (userData == null) {
            throw new IllegalStateException("toObject returned null, but doc exists");
        }

        if (userData.isArtist()) {
            ArtistData artistData = doc.toObject(ArtistData.class);

            if (artistData == null) {
                throw new IllegalStateException("toObject returned null, but doc exists");
            }

            return artistData.toUser(uid);
        }

        return userData.toUser(uid);
    }

}