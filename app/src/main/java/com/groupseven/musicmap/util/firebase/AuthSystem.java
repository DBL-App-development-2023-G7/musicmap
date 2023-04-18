package com.groupseven.musicmap.util.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.groupseven.musicmap.models.ArtistData;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.models.UserData;
import com.groupseven.musicmap.util.TaskUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AuthSystem {

    /**
     * This method adds the user and its attributes to the Firebase Firestore database.
     *
     * @param user the user to add to the Firestore database
     * @return the future
     */
    public static CompletableFuture<Void> addUserToFirestore(@NonNull User user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return addUserToFirestore(firestore, user);
    }

    /**
     * This overloaded method adds the user and its attributes to the Firebase Firestore database.
     *
     * @param firestore the FirebaseFirestore instance to use
     * @param user the user to add to the Firestore database
     * @return the future
     */
    public static CompletableFuture<Void> addUserToFirestore(@NonNull FirebaseFirestore firestore, @NonNull User user) {
        return TaskUtil.getFuture(firestore.collection("Users")
                .document(user.getUid())
                .set(user.getData())
        );
    }

    /**
     * This method registers a user using the Firebase Auth system. This method also add the date that is not contained
     * in the profile of the user to the Firestore Database and sends a verification email to the given user's email
     * address.
     *
     * @param userData the user to be registered
     * @param password the password of the user to be registered
     * @return the result of this task
     */
    public static CompletableFuture<Void> register(UserData userData, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = userData.getEmail();

        return Queries.getUserWithUsername(userData.getUsername())
                .thenCompose(user -> {
                    if (user != null) {
                        throw new IllegalArgumentException("The username already exist!");
                    }

                    CompletableFuture<AuthResult> registerAccount = TaskUtil.getFuture(
                            auth.createUserWithEmailAndPassword(email, password));

                    return registerAccount.thenCompose(result -> {
                        FirebaseUser firebaseUser = result.getUser();

                        if (firebaseUser != null) {
                            CompletableFuture<Void> sendEmail =
                                    TaskUtil.getFuture(firebaseUser.sendEmailVerification());
                            CompletableFuture<Void> addUser =
                                    addUserToFirestore(new User(userData, firebaseUser.getUid()));

                            return CompletableFuture.allOf(sendEmail, addUser);
                        }

                        throw new IllegalStateException("The firebase user is null");
                    });
                });
    }

    /**
     * This method logins a user using its username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the result of the task
     */
    public static CompletableFuture<AuthResult> loginWithUsernameAndPassword(String username, String password) {
        return Queries.getUserWithUsername(username).thenCompose(user -> {
            if (user == null) {
                throw new IllegalArgumentException("Username does not exist");
            }

            String email = user.getData().getEmail();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            return TaskUtil.getFuture(auth.signInWithEmailAndPassword(email, password));
        });
    }

    /**
     * This method tries to parse the given document as a UserData or ArtistData class.
     *
     * @param doc the given document from the firebase database
     * @return the doc formatted as a UserData class
     * @throws IllegalArgumentException if the given document does not exist
     * @throws NullPointerException     if the given document does not have any data
     */
    public static UserData parseUserData(DocumentSnapshot doc) throws IllegalArgumentException, NullPointerException {
        if (!doc.exists()) {
            throw new IllegalArgumentException("Document does not exist.");
        }

        UserData userData = doc.toObject(UserData.class);

        if (userData == null) {
            throw new NullPointerException("Document does not contain any data.");
        }

        if (userData.isArtist()) {
            return doc.toObject(ArtistData.class);
        }

        return userData;
    }

    /**
     * Gets the user data of the user that has the given {@code uid}.
     *
     * @param uid the given uid of the user
     * @return the result of the task
     */
    public static CompletableFuture<UserData> getUserData(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        return TaskUtil.getFuture(firestore.collection("Users").document(uid).get())
                .thenApply(AuthSystem::parseUserData);
    }

    /**
     * This method retrieves the user that has the given uid and their data.
     *
     * @param uid the given uid of the user
     * @return the result of this task
     */
    public static CompletableFuture<User> getUser(String uid) {
        return getUserData(uid).thenApply(userData -> userData.toUser(uid));
    }

    /**
     * This method removes the data stored in the Firestore database of the user that has the given uid. This method
     * is intentionally made private.
     *
     * @param uid the uid of the user
     * @return the future indicating when/if the removal is complete
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
     * This method updates the username of the with the one given in the {@code username} parameter.
     *
     * @param username the new username of the user
     * @return the result of the task
     */
    public static CompletableFuture<Void> updateUsername(String username) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            future.completeExceptionally(new FirebaseNoSignedInUserException("There is no user connected!"));
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
     * This method uploads the given photo as the profile picture of the currently connected user.
     *
     * @param photoUri the local uri of the photo that needs to be uploaded
     * @return the result of the task
     */
    public static CompletableFuture<Void> updateProfilePicture(Uri photoUri) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (firebaseUser == null) {
            future.completeExceptionally(new FirebaseNoSignedInUserException("There is no user connected!"));
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
     * This method updates the email of the currently connected user and if successful it will send a new
     * verification email.
     *
     * @param newEmail the new email
     * @param password the password of the connected user
     * @return the result of the task
     */
    public static CompletableFuture<Void> updateEmail(String newEmail, String password) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            future.completeExceptionally(new FirebaseNoSignedInUserException("There is no user connected!"));
            return future;
        }

        if (firebaseUser.getEmail() == null) {
            future.completeExceptionally(new IllegalStateException("The current user does not have an email address."));
            return future;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);

        return TaskUtil.getFuture(firebaseUser.reauthenticate(credential))
                .thenCompose(unused ->
                    TaskUtil.getFuture(firebaseUser.updateEmail(newEmail))
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
                            })
                );
        }

    /**
     * This method updated the password of the currently connected user. It replaces the old password with the one
     * provided in the parameter {@code oldPassword}.
     *
     * @param oldPassword the current password of the connected user
     * @param newPassword the new password
     * @return the result of the task
     */
    public static CompletableFuture<Void> updatePassword(String oldPassword, String newPassword) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            future.completeExceptionally(new FirebaseNoSignedInUserException("There is no user connected!"));
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
     * This method deletes the connected user and their data from the Firestore database.
     *
     * @return the result of this task
     */
    public static CompletableFuture<Void> deleteUser(String password) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            future.completeExceptionally(new FirebaseNoSignedInUserException("There is no user connected!"));
            return future;
        }

        if (firebaseUser.getEmail() == null) {
            future.completeExceptionally(new IllegalStateException("The current user does not have an email address."));
            return future;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        return TaskUtil.getFuture(firebaseUser.reauthenticate(credential))
                .thenCompose(unused -> removeUserDataFromFirestore(firebaseUser.getUid()))
                .thenCompose(unused -> TaskUtil.getFuture(firebaseUser.delete()));
    }

    /**
     * This method logs out the user.
     */
    public static void logout() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
    }

}