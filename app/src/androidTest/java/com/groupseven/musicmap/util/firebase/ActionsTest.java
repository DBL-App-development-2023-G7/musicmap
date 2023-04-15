package com.groupseven.musicmap.util.firebase;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.groupseven.musicmap.TestDataStore;
import com.groupseven.musicmap.models.MusicMemory;

import org.junit.Test;

/**
 * Test posting to the Firebase Firestore without actually posting to the live database,
 * through mocking.
 */
public class ActionsTest {
    @Test
    public void postMusicMemory_success() {
        MusicMemory musicMemory = TestDataStore.getValidMusicMemory();

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

}

