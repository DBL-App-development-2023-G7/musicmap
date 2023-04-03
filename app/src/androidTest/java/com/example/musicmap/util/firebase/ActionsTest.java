package com.example.musicmap.util.firebase;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.musicmap.feed.ConcertMemory;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.feed.Song;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

import java.util.Date;

public class ActionsTest {

    @Test
    public void postMusicMemory_success() {
        MusicMemory musicMemory = new MusicMemory("author-uid", new Date(),
                new GeoPoint(10, 10), "https://imgur.com/photo", new Song(
                "song", "1234", null, "https://imgur.com/photo-3", "https://spotify.com/preview"
        ));


        FirebaseFirestore firestoreMock = mock(FirebaseFirestore.class);
        CollectionReference collectionReferenceMock = mock(CollectionReference.class);
        DocumentReference documentReferenceMock = mock(DocumentReference.class);
        TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();

        when(firestoreMock.collection(eq("Users"))).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(eq(musicMemory.getAuthorUid()))).thenReturn(documentReferenceMock);
        when(documentReferenceMock.collection(eq("MusicMemories"))).thenReturn(collectionReferenceMock);

        when(collectionReferenceMock.add(eq(musicMemory))).thenReturn(taskCompletionSource.getTask());

        Task<?> task = Actions.postMusicMemory(firestoreMock, musicMemory);
        taskCompletionSource.setResult(documentReferenceMock);

        task.getResult();
        verify(collectionReferenceMock).add(eq(musicMemory));
    }

    @Test
    public void postConcertMemory_success() {
        ConcertMemory concertMemory = new ConcertMemory("author-uid", new Date(),
                new GeoPoint(10, 10), "name", "https://youtube.com/video");


        FirebaseFirestore firestoreMock = mock(FirebaseFirestore.class);
        CollectionReference collectionReferenceMock = mock(CollectionReference.class);
        DocumentReference documentReferenceMock = mock(DocumentReference.class);
        TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();

        when(firestoreMock.collection(eq("Users"))).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(eq(concertMemory.getAuthorUid()))).thenReturn(documentReferenceMock);
        when(documentReferenceMock.collection(eq("ConcertMemories"))).thenReturn(collectionReferenceMock);

        when(collectionReferenceMock.add(eq(concertMemory))).thenReturn(taskCompletionSource.getTask());

        Task<?> task = Actions.postConcertMemory(firestoreMock, concertMemory);
        taskCompletionSource.setResult(documentReferenceMock);

        task.getResult();
        verify(collectionReferenceMock).add(eq(concertMemory));
    }

}

