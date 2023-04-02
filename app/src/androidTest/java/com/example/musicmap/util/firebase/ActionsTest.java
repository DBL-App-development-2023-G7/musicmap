package com.example.musicmap.util.firebase;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.musicmap.feed.ConcertMemory;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.feed.Song;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class ActionsTest {

    private FirebaseFirestore firestore;
    private CollectionReference usersCollectionRef;
    private DocumentReference documentRef;

    @Before
    public void setup() {
        firestore = Mockito.mock(FirebaseFirestore.class);
        usersCollectionRef = Mockito.mock(CollectionReference.class);
        documentRef = Mockito.mock(DocumentReference.class);
    }

    @Test
    public void postMusicMemory_success() throws InterruptedException {
        String authorUid = "author-uid";
        when(firestore.collection("Users")).thenReturn(usersCollectionRef);
        when(usersCollectionRef.document(authorUid)).thenReturn(documentRef);
        when(documentRef.collection("MusicMemories")).thenReturn(Mockito.mock(CollectionReference.class));
        when(documentRef.collection("MusicMemories").add(any(MusicMemory.class))).thenAnswer(invocation -> Tasks.forResult(null));

        MusicMemory musicMemory = new MusicMemory("author-uid", new Date(),
                new GeoPoint(10, 10), "https://imgur.com/photo", new Song(
                "song", "1234", null, "https://imgur.com/photo-3", "https://spotify.com/preview"
        ));
        Task<?> task = Actions.postMusicMemory(musicMemory);

        CountDownLatch latch = new CountDownLatch(1);
        task.addOnCompleteListener(result -> {
            assertTrue(result.isSuccessful());
            assertNull(result.getException());
            latch.countDown();
        });

        latch.await();
    }


    @Test
    public void postConcertMemory_success() throws InterruptedException {
        String authorUid = "author-uid";
        when(firestore.collection("Users")).thenReturn(usersCollectionRef);
        when(usersCollectionRef.document(authorUid)).thenReturn(documentRef);
        when(documentRef.collection("ConcertMemories")).thenReturn(Mockito.mock(CollectionReference.class));
        when(documentRef.collection("ConcertMemories").add(any(ConcertMemory.class))).thenAnswer(invocation -> Tasks.forResult(null));

        ConcertMemory concertMemory = new ConcertMemory("author-uid", new Date(),
                new GeoPoint(10, 10), "name", "https://youtube.com/video");
        Task<?> task = Actions.postConcertMemory(concertMemory);

        CountDownLatch latch = new CountDownLatch(1);
        task.addOnCompleteListener(result -> {
            assertTrue(result.isSuccessful());
            assertNull(result.getException());
            latch.countDown();
        });

        latch.await();
    }

}

