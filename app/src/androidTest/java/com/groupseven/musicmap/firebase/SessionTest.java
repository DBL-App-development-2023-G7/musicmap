package com.groupseven.musicmap.firebase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class SessionTest {

    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;

    private FirebaseFirestore firestore;

    private DocumentReference docRef;

    private DocumentSnapshot docSnapshot;

    @Before
    public void setup() {
        firebaseAuth = mock(FirebaseAuth.class);
        firebaseUser = mock(FirebaseUser.class);
        firestore = mock(FirebaseFirestore.class);
        docRef = mock(DocumentReference.class);
        docSnapshot = mock(DocumentSnapshot.class);
    }

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

        assertTrue(session.getListeners().contains(listener));
    }

    @Test
    public void testRemoveListener() {
        Session session = Session.getInstance();
        Session.Listener listener = mock(Session.Listener.class);

        session.addListener(listener);
        session.removeListener(listener);

        assertFalse(session.getListeners().contains(listener));
    }

    @Test
    public void testUpdateListeners() {
        Session session = Session.getInstance();

        Session.Listener listener1 = mock(Session.Listener.class);
        Session.Listener listener2 = mock(Session.Listener.class);
        Set<Session.Listener> listeners = new HashSet<>();
        listeners.add(listener1);
        listeners.add(listener2);
        session.getListeners().addAll(listeners);

        session.updateListeners();

        verify(listener1, times(1)).onSessionStateChanged();
        verify(listener2, times(1)).onSessionStateChanged();
    }

    @Test
    public void testOnAuthStateChanged_userConnected() {
        Session session = Session.getInstance();

        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("test");
        when(firestore.document(anyString())).thenReturn(docRef);
        when(docRef.addSnapshotListener(any())).thenReturn(mock(ListenerRegistration.class));
        when(docSnapshot.getId()).thenReturn("test");

        session.onAuthStateChanged(firebaseAuth);
        assertTrue(session.isUserConnected());
        assertNotNull(session.getUserListenerRegistration());
    }

    @Test
    public void testOnAuthStateChanged_userNotConnected() {
        Session session = Session.getInstance();

        when(firebaseAuth.getCurrentUser()).thenReturn(null);
        when(firebaseUser.getUid()).thenReturn(null);
        when(firestore.document(anyString())).thenReturn(docRef);
        when(docRef.addSnapshotListener(any())).thenReturn(mock(ListenerRegistration.class));
        when(docSnapshot.getId()).thenReturn("test");

        session.onAuthStateChanged(firebaseAuth);
        assertFalse(session.isUserConnected());
        assertNull(session.getUserListenerRegistration());
    }

}

