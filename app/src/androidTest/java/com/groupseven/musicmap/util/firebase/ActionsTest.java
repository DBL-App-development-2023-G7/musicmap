package com.groupseven.musicmap.util.firebase;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.groupseven.musicmap.TestDataStore;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.util.TaskUtil;

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

        Task<?> task = TaskUtil.getTask(Actions.postMusicMemory(firestoreMock, musicMemory));
        taskCompletionSource.setResult(documentReferenceMock);
        task.getResult();
        verify(collectionReferenceMock).add(eq(musicMemory));
    }

    @Test
    public void testUploadMusicMemoryImage_success() {
        FirebaseStorage firebaseStorageMock = mock(FirebaseStorage.class);
        StorageReference rootReferenceMock = mock(StorageReference.class);
        Bitmap capturedImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        capturedImage.setPixels(new int[10000], 0, 100, 0, 0, 100, 100);
        String authorUid = "testAuthorUid";
        String randomUuid = "testRandomUuid";

        when(firebaseStorageMock.getReference()).thenReturn(rootReferenceMock);
        when(rootReferenceMock.child(anyString())).thenReturn(mock(StorageReference.class));
        when(rootReferenceMock.child(String.format("users/%s/memories/%s.jpg", authorUid, randomUuid)))
                .thenReturn(mock(StorageReference.class));
        when(rootReferenceMock.child(String.format("users/%s/memories/%s.jpg", authorUid, randomUuid))
                .getDownloadUrl()).thenReturn(Tasks.forResult(mock(Uri.class)));

        Actions.uploadMusicMemoryImage(firebaseStorageMock, capturedImage, authorUid);
        verify(rootReferenceMock).child(String.format("users/%s/memories/%s.jpg", authorUid, randomUuid));
    }

}

