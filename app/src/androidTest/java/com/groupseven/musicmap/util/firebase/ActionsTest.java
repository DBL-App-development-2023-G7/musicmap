package com.groupseven.musicmap.util.firebase;

/**
 * Test posting to the Firebase Firestore without actually posting to the live database,
 * through mocking.
 */
public class ActionsTest {
//    @Test
//    public void postMusicMemory_success() {
//        MusicMemory musicMemory = TestDataStore.getValidMusicMemory();
//
//        FirebaseFirestore firestoreMock = mock(FirebaseFirestore.class);
//        CollectionReference collectionReferenceMock = mock(CollectionReference.class);
//        DocumentReference documentReferenceMock = mock(DocumentReference.class);
//        TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();
//
//        when(firestoreMock.collection(eq("Users"))).thenReturn(collectionReferenceMock);
//        when(collectionReferenceMock.document(eq(musicMemory.getAuthorUid()))).thenReturn(documentReferenceMock);
//        when(documentReferenceMock.collection(eq("MusicMemories"))).thenReturn(collectionReferenceMock);
//
//        when(collectionReferenceMock.add(eq(musicMemory))).thenReturn(taskCompletionSource.getTask());
//
//        Task<?> task = Actions.postMusicMemory(musicMemory);
//        taskCompletionSource.setResult(documentReferenceMock);
//
//        task.getResult();
//        verify(collectionReferenceMock).add(eq(musicMemory));
//    }

}

