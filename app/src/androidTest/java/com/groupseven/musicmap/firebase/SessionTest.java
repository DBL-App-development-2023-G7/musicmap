package com.groupseven.musicmap.firebase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class SessionTest {

    @Test
    public void testSingletonInstance() {
        Session instance1 = Session.getInstance();
        Session instance2 = Session.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    public void testAddListener() {
        Session session = Session.getInstance();
        Session.Listener listener = mock(Session.Listener.class);

        session.addListener(listener);

        assertTrue(session.listeners.contains(listener));
    }

    @Test
    public void testRemoveListener() {
        Session session = Session.getInstance();
        Session.Listener listener = mock(Session.Listener.class);

        session.addListener(listener);
        session.removeListener(listener);

        assertFalse(session.listeners.contains(listener));
    }

    @Test
    public void testOnAuthStateChanged() {
        Session session = Session.getInstance();

        FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        FirebaseFirestore firestore = mock(FirebaseFirestore.class);
        DocumentReference docRef = mock(DocumentReference.class);
        DocumentSnapshot docSnapshot = mock(DocumentSnapshot.class);

        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("test");
        when(firestore.document(anyString())).thenReturn(docRef);
        when(docRef.addSnapshotListener(any())).thenReturn(mock(ListenerRegistration.class));
        when(docSnapshot.getId()).thenReturn("test");

        // Test case where nothing is null
        session.onAuthStateChanged(firebaseAuth);
        assertTrue(session.isUserConnected());
        assertNotNull(session.getCurrentUser());
        assertNotNull(session.userListenerRegistration);

        // Test case where firebaseUser is not null and userListenerRegistration is null
        session.onAuthStateChanged(firebaseAuth);
        assertTrue(session.isUserConnected());
        assertNotNull(session.userListenerRegistration);

        // Test case where firebaseUser is not null and userListenerRegistration is not null
        session.onAuthStateChanged(firebaseAuth);
        assertTrue(session.isUserConnected());
        assertNotNull(session.userListenerRegistration);
    }

    @Test
    public void testUpdateListeners() {
        Session session = Session.getInstance();

        Session.Listener listener1 = mock(Session.Listener.class);
        Session.Listener listener2 = mock(Session.Listener.class);
        Set<Session.Listener> listeners = new HashSet<>();
        listeners.add(listener1);
        listeners.add(listener2);
        session.listeners.addAll(listeners);

        session.updateListeners();

        verify(listener1, times(1)).onSessionStateChanged();
        verify(listener2, times(1)).onSessionStateChanged();
    }

}

