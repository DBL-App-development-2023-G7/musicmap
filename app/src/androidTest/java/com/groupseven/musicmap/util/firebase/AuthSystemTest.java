package com.groupseven.musicmap.util.firebase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.groupseven.musicmap.TestDataStore;
import com.groupseven.musicmap.models.User;

import org.junit.Test;

public class AuthSystemTest {

    @Test
    public void testAddUserToFirestore() {
        FirebaseFirestore firestore = mock(FirebaseFirestore.class);
        CollectionReference mockCollectionReference = mock(CollectionReference.class);
        DocumentReference mockDocumentReference = mock(DocumentReference.class);

        when(firestore.collection("Users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(any())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null));

        User user = TestDataStore.getValidUser();
        AuthSystem.addUserToFirestore(firestore, user).join();

        verify(firestore).collection("Users");
        verify(firestore.collection("Users")).document(user.getUid());
        verify(firestore.collection("Users").document(user.getUid())).set(user.getData());
    }

}
