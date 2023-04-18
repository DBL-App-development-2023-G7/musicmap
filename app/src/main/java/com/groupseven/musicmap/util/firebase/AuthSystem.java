package com.groupseven.musicmap.util.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.groupseven.musicmap.MusicMap;
import com.groupseven.musicmap.R;
import com.groupseven.musicmap.models.ArtistData;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.models.UserData;
import com.groupseven.musicmap.util.conversion.TaskUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Utility for interacting with everything related to authentication and accounts.
 */
public class AuthSystem {

    /**
     * Adds the user and its attributes to the database.
     *
     * @param user the user to add to the database.
     * @return the future indicating when/if the user is added.
     */
    public static CompletableFuture<Void> addUserToFirestore(@NonNull User user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return addUserToFirestore(firestore, user);
    }

    /**
     * Adds the user and its attributes to the database.
     *
     * @param firestore the FirebaseFirestore instance to use.
     * @param user the user to add to the database.
     * @return the future indicating when/if the user is added.
     */
    public static CompletableFuture<Void> addUserToFirestore(@NonNull FirebaseFirestore firestore, @NonNull User user) {
        return TaskUtil.getFuture(firestore.collection("Users")
                .document(user.getUid())
                .set(user.getData())
        );
    }

    /**
     * Registers a user.
     * <p>
     * Sends a verification email to the given user's email address.
     *
     * @param userData the user to be registered
     * @param password the password of the user to be registered
     * @return the future indicating when/if the user is registered
     */
    public static CompletableFuture<Void> register(UserData userData, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = userData.getEmail();

        return Queries.getUserWithUsername(userData.getUsername())
                .thenCompose(user -> {
                    if (user != null) {
                        throw new IllegalArgumentException(
                                MusicMap.getAppResources().getString(R.string.input_error_username_exists));
                    }

                    return TaskUtil.getFuture(auth.createUserWithEmailAndPassword(email, password));
                })
                .thenCompose(result -> {
                    FirebaseUser firebaseUser = result.getUser();

                    if (firebaseUser == null) {
                        throw new IllegalStateException("The firebase user is null");
                    }

                    CompletableFuture<Void> sendEmail =
                            TaskUtil.getFuture(firebaseUser.sendEmailVerification());
                    CompletableFuture<Void> addUser =
                            addUserToFirestore(new User(userData, firebaseUser.getUid()));

                    return CompletableFuture.allOf(sendEmail, addUser);

                });
    }

    /**
     * Attempts to log in to a user.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     * @return the future indicating when/if the login was successful.
     */
    public static CompletableFuture<Void> loginWithUsernameAndPassword(String username, String password) {
        return Queries.getUserWithUsername(username).thenCompose(user -> {
            if (user == null) {
                throw new IllegalArgumentException(
                        MusicMap.getAppResources().getString(R.string.username_does_not_exist));
            }

            String email = user.getData().getEmail();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            return TaskUtil.getFuture(auth.signInWithEmailAndPassword(email, password))
                    .thenApply(authResult -> null);
        });
    }

    /**
     * Gets the user data of the user with the given uid.
     *
     * @param uid the given uid of the user.
     * @return the future with the user data.
     */
    public static CompletableFuture<UserData> getUserData(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        return TaskUtil.getFuture(firestore.collection("Users").document(uid).get())
                .thenApply(AuthSystem::parseUserData);
    }

    /**
     * Retrieves the user with the given uid and their data.
     *
     * @param uid the given uid of the user.
     * @return the future with the user.
     */
    public static CompletableFuture<User> getUser(String uid) {
        return getUserData(uid).thenApply(userData -> userData.toUser(uid));
    }

    /**
     * Updates the username of the currently connected user.
     *
     * @param username the new username.
     * @return the future indicating when/if the username is updated.
     */
    public static CompletableFuture<Void> updateUsername(String username) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            future.completeExceptionally(new IllegalStateException("There is no user connected!"));
            return future;
        }

        DocumentReference documentReference =
                firestore.collection("Users").document(firebaseUser.getUid());

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);

        return Queries.getUserWithUsername(username).thenCompose(user -> {
            if (user != null) {
                throw new IllegalArgumentException("That username already exists");
            }

            return TaskUtil.getFuture(documentReference.update(data));
        });
    }

    /**
     * Updates the profile picture of the currently connected user.
     *
     * @param photoUri the local uri of the new profile picture.
     * @return the future indicating when/if the profile picture is updated.
     */
    public static CompletableFuture<Void> updateProfilePicture(Uri photoUri) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (firebaseUser == null) {
            future.completeExceptionally(new IllegalStateException("There is no user connected!"));
            return future;
        }

        StorageReference storageRef = storage.getReference("users/" + firebaseUser.getUid());
        StorageReference folderRef = storageRef.child("/profilePicture");
        StorageReference photoRef = folderRef.child(photoUri.getLastPathSegment());

        return TaskUtil.getFuture(folderRef.listAll())
                .thenCompose(listResult -> {
                    // Delete older photos
                    return CompletableFuture.allOf(listResult.getItems().stream()
                            .map(StorageReference::delete)
                            .map(TaskUtil::getFuture)
                            .toArray(CompletableFuture[]::new)
                    );
                })
                .thenCompose(unused -> TaskUtil.getFuture(photoRef.putFile(photoUri)))
                .thenCompose(unused -> TaskUtil.getFuture(photoRef.getDownloadUrl()))
                .thenCompose(uri -> {
                    DocumentReference documentReference =
                            firestore.collection("Users").document(firebaseUser.getUid());
                    Map<String, Object> data = new HashMap<>();
                    data.put("profilePicture", uri);

                    return TaskUtil.getFuture(documentReference.update(data));
                });
    }

    /**
     * Updates the email of the currently connected user.
     * <p>
     * This will send a verification email as well, if successful.
     * <p>
     * Requires the password of the user for security purposes.
     *
     * @param newEmail the new email.
     * @param password the password.
     * @return the future indicating when/if the email is updated.
     */
    public static CompletableFuture<Void> updateEmail(String newEmail, String password) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            future.completeExceptionally(new IllegalStateException("There is no user connected!"));
            return future;
        }

        if (firebaseUser.getEmail() == null) {
            future.completeExceptionally(new IllegalStateException("The current user does not have an email address."));
            return future;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);

        return TaskUtil.getFuture(firebaseUser.reauthenticate(credential))
                .thenCompose(unused -> TaskUtil.getFuture(firebaseUser.updateEmail(newEmail)))
                .thenCompose(unused1 -> {
                    DocumentReference documentReference =
                            firestore.collection("Users").document(firebaseUser.getUid());
                    Map<String, Object> data = new HashMap<>();
                    data.put("email", newEmail);

                    CompletableFuture<Void> sendEmail = TaskUtil.getFuture(
                            firebaseUser.sendEmailVerification());
                    CompletableFuture<Void> updateEmail = TaskUtil.getFuture(
                            documentReference.update(data));

                    return CompletableFuture.allOf(sendEmail, updateEmail);
                });
        }

    /**
     * Updates the password of the currently connected user.
     * <p>
     * Replaces the old password, which must be provided as well for security purposes.
     *
     * @param oldPassword the current password.
     * @param newPassword the new password.
     * @return the future indicating when/if the password is updated.
     */
    public static CompletableFuture<Void> updatePassword(String oldPassword, String newPassword) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            future.completeExceptionally(new IllegalStateException("There is no user connected!"));
            return future;
        }

        if (firebaseUser.getEmail() == null) {
            future.completeExceptionally(new IllegalStateException("The current user does not have an email address."));
            return future;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);
        return TaskUtil.getFuture(firebaseUser.reauthenticate(credential))
                .thenCompose(unused -> TaskUtil.getFuture(firebaseUser.updatePassword(newPassword)));
    }

    /**
     * Deletes the connected user and their data from the database.
     *
     * @return the future indicating when/if the user is deleted.
     */
    public static CompletableFuture<Void> deleteUser(String password) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            future.completeExceptionally(new IllegalStateException("There is no user connected!"));
            return future;
        }

        if (firebaseUser.getEmail() == null) {
            future.completeExceptionally(new IllegalStateException("The current user does not have an email address."));
            return future;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);

        // First, re-authenticate
        return TaskUtil.getFuture(firebaseUser.reauthenticate(credential))
                // Remove user data
                .thenCompose(unused -> removeUserDataFromFirestore(firebaseUser.getUid()))
                // Then delete user
                .thenCompose(unused -> TaskUtil.getFuture(firebaseUser.delete()));
    }

    /**
     * Removes the data stored in the database of the user with the given uid.
     *
     * @param uid the uid of the user.
     * @return the future indicating when/if the user is removed.
     */
    private static CompletableFuture<Void> removeUserDataFromFirestore(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return TaskUtil.getFuture(firestore.collection("Users/" + uid + "/MusicMemories").get())
                .thenCompose(memories -> {
                    WriteBatch batch = firestore.batch();
                    for (QueryDocumentSnapshot memory : memories) {
                        batch.delete(memory.getReference());
                    }

                    return TaskUtil.getFuture(batch.commit())
                            .thenCompose(unused -> TaskUtil.getFuture(
                                    firestore.collection("Users").document(uid).delete()));
                });
    }

    /**
     * Logs out the user.
     */
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Parses the given document as an appropriate {@link UserData}.
     * <p>
     * This will return an {@link ArtistData} if the given document contains the data of an artist.
     *
     * @param doc the given document from the database.
     * @return the user data.
     * @throws IllegalArgumentException if the given document {@link DocumentSnapshot#exists() does not exist}.
     */
    public static UserData parseUserData(DocumentSnapshot doc) throws IllegalArgumentException {
        if (!doc.exists()) {
            throw new IllegalArgumentException("Document does not exist.");
        }

        UserData userData = doc.toObject(UserData.class);

        if (userData == null) {
            throw new IllegalStateException("toObject returned null for existing document");
        }

        if (userData.isArtist()) {
            return doc.toObject(ArtistData.class);
        }

        return userData;
    }

}