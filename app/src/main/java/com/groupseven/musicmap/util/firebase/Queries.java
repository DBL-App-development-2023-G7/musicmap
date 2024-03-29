package com.groupseven.musicmap.util.firebase;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.groupseven.musicmap.models.ArtistData;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.models.Post;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.models.SongCount;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.models.UserData;
import com.groupseven.musicmap.util.conversion.TaskUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Utility class for making queries to the Firestore database.
 */
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
                                "More than one user with username '" + username + "' exist");
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
     * @param authorUid the id of the author.
     * @param uid the id of the music memory.
     * @return the future containing the music memory.
     */
    public static CompletableFuture<MusicMemory> getMusicMemoryByAuthorIdAndId(String authorUid, String uid) {
        /*
        Ideally we'd use the collection group MusicMemories here, which does not require providing the author uid,
        but due to a Firestore index usage this cannot be done without additional modifications:
        https://stackoverflow.com/questions/56149601/firestore-collection-group-query-on-documentid/58104104#58104104
         */
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return TaskUtil.getFuture(firestore.collection("Users")
                        .document(authorUid)
                        .collection("MusicMemories")
                        .document(uid)
                        .get())
                .thenApply(documentSnapshot -> deserialize(documentSnapshot, MusicMemory.class));
    }

    /**
     * Fetches all the music memories for an author.
     *
     * @param authorUid the id of the author.
     * @return a future containing all music memories of the author.
     */
    public static CompletableFuture<List<MusicMemory>> getMusicMemoriesByAuthorId(String authorUid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return TaskUtil.getFuture(firestore.collection("Users")
                        .document(authorUid)
                        .collection("MusicMemories").get())
                .thenApply(querySnapshot -> querySnapshot.getDocuments()
                        .stream()
                        .map(document -> deserialize(document, MusicMemory.class))
                        .collect(Collectors.toList()));
    }

    /**
     * Fetches all the music memories created in the last 24 hours.
     *
     * @return music memories in last 24 hours.
     */
    public static CompletableFuture<List<MusicMemory>> getAllMusicMemoriesInLastTwentyFourHours() {
        long timestamp24HoursAgo = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24);

        return getAllMusicMemories(Filter.greaterThan("timePosted", new Date(timestamp24HoursAgo)));
    }

    /**
     * Fetches all the music memories made with songs of a certain Spotify artist.
     *
     * @param spotifyArtistId the id of the Spotify artist.
     * @return all music memories with songs of the given artist.
     */
    public static CompletableFuture<List<MusicMemory>> getAllMusicMemoriesWithSpotifyArtistId(String spotifyArtistId) {
        return getAllMusicMemories(Filter.equalTo("song.spotifyArtistId", spotifyArtistId));
    }

    /**
     * Fetches the most popular songs for an artist.
     *
     * @param artistId the id of the artist
     * @param count the number of songs to return (or all, whichever less)
     * @return {@code count} number of most popular songs for the artist
     */
    public static CompletableFuture<List<SongCount>> getMostPopularSongsByArtist(String artistId, int count) {
        return getAllMusicMemoriesWithSpotifyArtistId(artistId)
                .thenApply(musicMemories -> {
                    // Keep track of how often each song occurs
                    Map<Song, Long> songMap = new HashMap<>();

                    for (MusicMemory musicMemory : musicMemories) {
                        Song song = musicMemory.getSong();

                        if (song != null) {
                            //noinspection ConstantConditions
                            songMap.put(song, songMap.getOrDefault(song, 0L) + 1);
                        }
                    }

                    // Sort by how often the songs occur, and turn them into SongCount
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
    private static CompletableFuture<List<MusicMemory>> getAllMusicMemories(Filter filter) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        return TaskUtil.getFuture(firestore.collectionGroup("MusicMemories")
                        .where(filter)
                        .get())
                .thenApply(querySnapshot -> querySnapshot.getDocuments()
                        .stream()
                        .map(document -> deserialize(document, MusicMemory.class))
                        .collect(Collectors.toList()));
    }

    /**
     * Deserializes the given document into a post of the given class.
     *
     * @param document the document snapshot (which must {@link DocumentSnapshot#exists() exist}.
     * @param postClass the class of the post, e.g. {@code MusicMemory.class}.
     * @return the deserialized post, or {@code null}
     * @param <P> the type of post, e.g. {@link MusicMemory}.
     */
    private static <P extends Post> P deserialize(DocumentSnapshot document, Class<P> postClass) {
        if (document == null) {
            throw new NullPointerException("document");
        }
        if (!document.exists()) {
            throw new IllegalArgumentException("The given document does not exist");
        }
        if (postClass == null) {
            throw new NullPointerException("postClass");
        }

        P post = document.toObject(postClass);

        if (post == null) {
            throw new IllegalStateException("toObject returned null, but doc exists");
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
     * @param document the document snapshot (which must {@link DocumentSnapshot#exists() exist}.
     * @return the user.
     */
    private static User deserializeUser(DocumentSnapshot document) throws IllegalStateException {
        if (document == null) {
            throw new NullPointerException("document");
        }
        if (!document.exists()) {
            throw new IllegalArgumentException("The given document does not exist");
        }

        String uid = document.getReference().getId();
        UserData userData = document.toObject(UserData.class);

        if (userData == null) {
            throw new IllegalStateException("toObject returned null, but doc exists");
        }

        // Re-parse as ArtistData in case it is the data of an artist
        if (userData.isArtist()) {
            ArtistData artistData = document.toObject(ArtistData.class);

            if (artistData == null) {
                throw new IllegalStateException("toObject returned null, but doc exists");
            }

            return artistData.toUser(uid);
        }

        return userData.toUser(uid);
    }

}