package com.groupseven.musicmap.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;

import java.util.HashSet;
import java.util.Set;

/**
 * The Session Singleton class.
 * <p>
 * This class holds information regarding the current connected user.
 */
public final class Session implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "Session";
    private static volatile Session instance;

    final Set<Listener> listeners;

    private User currentUser;
    private boolean userConnected;

    ListenerRegistration userListenerRegistration;

    private Session() {
        listeners = new HashSet<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(this);
        userConnected = firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Retrieves the instance of the Session.
     *
     * @return the singleton instance of the Session class
     */
    public static Session getInstance() {
        if (instance == null) {
            synchronized (Session.class) {
                if (instance == null) {
                    Log.i(TAG, "Generating a session instance.");
                    instance = new Session();
                }
            }
        }
        return instance;
    }

    /**
     * Retrieves the current connected user.
     *
     * @return the current connected user, or {@code null} if no user is connected.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserConnected() {
        return userConnected;
    }

    @EnsuresNonNullIf(expression = {"this.currentUser", "this.getCurrentUser()"}, result = true)
    public boolean isUserLoaded() {
        return currentUser != null && isUserConnected();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.i(TAG, "Session has been notified about the auth state change.");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            userConnected = false;
            currentUser = null;
            if (userListenerRegistration != null) {
                userListenerRegistration.remove();
                userListenerRegistration = null;
            }
            updateListeners();
        } else {
            userConnected = true;
            if (userListenerRegistration == null) {
                DocumentReference userDocRef = firestore.collection("Users").document(firebaseUser.getUid());
                userListenerRegistration = userDocRef.addSnapshotListener(this::refreshUserData);
            }
        }
    }

    void refreshUserData(DocumentSnapshot doc, FirebaseFirestoreException error) {
        Log.i(TAG, "Trying to refresh user's data");
        if (error != null) {
            Log.e(TAG, "Exception occurred while refreshing user data", error);
            return;
        }

        try {
            currentUser = AuthSystem.parseUserData(doc).toUser(doc.getId());
            Log.i(TAG, "User's data was refreshed.");
            updateListeners();
        } catch (Exception e) {
            Log.e(TAG, "Exception occurred while parsing retrieved user data", e);
        }
    }

    /**
     * Listener called when there is a change in the authentication state or in the user's data.
     * <p>
     * Use {@link #addListener(Listener)} and {@link #removeListener(Listener)} to register or
     * unregister listeners.
     */
    public interface Listener {
        void onSessionStateChanged();
    }

    /**
     * Adds the given {@code sessionListener} to the set of listener to be notified by the Session class.
     *
     * @param listener the listener object you want to add to the set
     */
    public void addListener(@NonNull Listener listener) {
        listeners.add(listener);
        listener.onSessionStateChanged();
    }

    /**
     * Removes the given {@code sessionListener} from the set of listener to be notified by the Session class.
     *
     * @param listener the listener object you want to remove from the set
     */
    public void removeListener(@NonNull Listener listener) {
        listeners.remove(listener);
    }

    void updateListeners() {
        Log.i(TAG, "Updating all session listeners.");
        for (Listener listener : listeners) {
            try {
                listener.onSessionStateChanged();
            } catch (Exception e) {
                Log.e(TAG, listener.getClass().getName() + " threw exception during listener call", e);
            }
        }
    }

}

