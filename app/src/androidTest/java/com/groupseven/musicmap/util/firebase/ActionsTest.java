package com.groupseven.musicmap.util.firebase;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.groupseven.musicmap.TestDataStore;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.util.TaskUtil;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

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
    public void testUploadMusicMemoryImage() {
       
    }

}

