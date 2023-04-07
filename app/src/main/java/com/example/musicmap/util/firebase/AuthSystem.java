package com.example.musicmap.util.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.musicmap.user.ArtistData;
import com.example.musicmap.user.User;
import com.example.musicmap.user.UserData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthSystem {

    /**
     * This method adds the user and its attributes to the Firebase Firestore database.
     *
     * @param user the user to add to the Firestore database
     * @return the task
     */
    public static Task<Void> addUserToFirestore(@NonNull User user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(user.getUid()).set(user.getData());
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
    public static Task<Void> register(UserData userData, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = userData.getEmail();

        Task<AuthResult> registerAccount = auth.createUserWithEmailAndPassword(email, password);
        return registerAccount.onSuccessTask(result -> {
            TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
            FirebaseUser firebaseUser = result.getUser();

            if (firebaseUser != null) {
                Task<Void> sendEmail = firebaseUser.sendEmailVerification();
                Task<Void> addUser = addUserToFirestore(new User(userData, firebaseUser.getUid()));
                return Tasks.whenAll(sendEmail, addUser);
            }
            tcs.setException(new IllegalStateException("The firebaseUser is null."));
            return tcs.getTask();
        });
    }

    /**
     * This method logins a user using its username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the result of the task
     */
    public static Task<AuthResult> loginWithUsernameAndPassword(String username, String password) {

        return Queries.getUsersWithUsername(username).onSuccessTask(docs -> {
            TaskCompletionSource<AuthResult> tcs = new TaskCompletionSource<>();

            if (docs.isEmpty()) {
                tcs.setException(new FirebaseFirestoreException("Query did not return any docs.",
                        FirebaseFirestoreException.Code.NOT_FOUND));
                return tcs.getTask();
            }

            DocumentSnapshot doc = docs.getDocuments().get(0);

            if (doc.getData() == null || doc.getData().isEmpty()) {
                tcs.setException(new FirebaseFirestoreException("Document does not exist or is empty.",
                        FirebaseFirestoreException.Code.NOT_FOUND));
                return tcs.getTask();
            }

            Object emailFirebaseField = doc.getData().get("email");
            if (!(emailFirebaseField instanceof String)) {
                tcs.setException(new IllegalArgumentException("The email field of the user is null or invalid."));
                return tcs.getTask();
            }

            String email = (String) emailFirebaseField;
            FirebaseAuth auth = FirebaseAuth.getInstance();

            return auth.signInWithEmailAndPassword(email, password);
        });
    }

    /**
     * This method tries to parse the given document as a UserData or ArtistData class.
     *
     * @param doc the given document from the firebase database
     * @return the doc formatted as a UserData class
     * @throws FirebaseFirestoreException if the given document does not exist
     * @throws NullPointerException       if the given document does not have any data
     */
    public static UserData parseUserData(DocumentSnapshot doc) throws FirebaseFirestoreException, NullPointerException {
        if (!doc.exists()) {
            throw new FirebaseFirestoreException("Document does not exist.", FirebaseFirestoreException.Code.NOT_FOUND);
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
    public static Task<UserData> getUserData(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Task<DocumentSnapshot> getUserDataFirestore = firestore.collection("Users").document(uid).get();

        return getUserDataFirestore.onSuccessTask(doc -> {
            TaskCompletionSource<UserData> tcs = new TaskCompletionSource<>();

            try {
                tcs.setResult(parseUserData(doc));
            } catch (Exception exception) {
                tcs.setException(exception);
            }

            return tcs.getTask();
        });
    }

    /**
     * This method retrieves the user that has the given uid and their data.
     *
     * @param uid the given uid of the user
     * @return the result of this task
     */
    public static Task<User> getUser(String uid) {
        return getUserData(uid).onSuccessTask(userData -> {
            TaskCompletionSource<User> tcs = new TaskCompletionSource<>();
            tcs.setResult(userData.toUser(uid));
            return tcs.getTask();
        });
    }

    /**
     * This method removes the data stored in the Firestore database of the user that has the given uid. This method
     * is intentionally made private.
     *
     * @param uid the uid of the user
     * @return the result of this task
     */
    private static Task<Void> removeUserDataFromFirestore(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users/" + uid + "/MusicMemories").get().onSuccessTask(memories -> {
            WriteBatch batch = firestore.batch();
            for (QueryDocumentSnapshot memory : memories) {
                batch.delete(memory.getReference());
            }
            
            return batch.commit().addOnSuccessListener(result ->
                    firestore.collection("Users").document(uid).delete());
        });
    }

    /**
     * This method updates the username of the with the one given in the {@code username} parameter.
     *
     * @param username the new username of the user
     * @return the result of the task
     */
    public static Task<Void> updateUsername(String username) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            tcs.setException(new FirebaseNoSignedInUserException("There is no user connected!"));
            return tcs.getTask();
        }

        DocumentReference documentReference =
                firestore.collection("Users").document(firebaseUser.getUid());

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);

        return Queries.getUsersWithUsername(username).onSuccessTask(results -> {
            if (!results.isEmpty()) {
                tcs.setException(new FirebaseFirestoreException("The username already exists",
                        FirebaseFirestoreException.Code.ALREADY_EXISTS));
                return tcs.getTask();
            }
            return documentReference.update(data);
        });
    }

    /**
     * This method uploads the given photo as the profile picture of the currently connected user.
     *
     * @param photoUri the local uri of the photo that needs to be uploaded
     * @return the result of the task
     */
    public static Task<Void> updateProfilePicture(Uri photoUri) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (firebaseUser == null) {
            tcs.setException(new FirebaseNoSignedInUserException("There is no user connected!"));
            return tcs.getTask();
        }

        StorageReference storageRef = storage.getReference("users/" + firebaseUser.getUid());
        StorageReference folderRef = storageRef.child("/profilePicture");
        StorageReference photoRef = folderRef.child(photoUri.getLastPathSegment());

        return folderRef.listAll().onSuccessTask(results -> {
            //Delete older photos
            for (StorageReference item : results.getItems()) {
                item.delete();
            }

            return photoRef.putFile(photoUri).onSuccessTask(taskSnapshot -> photoRef.getDownloadUrl()
                    .onSuccessTask(uri -> {
                        DocumentReference documentReference =
                                firestore.collection("Users").document(firebaseUser.getUid());
                        Map<String, Object> data = new HashMap<>();
                        data.put("profilePicture", uri);

                        return documentReference.update(data);
                    }));
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
    public static Task<Void> updateEmail(String newEmail, String password) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) {
            tcs.setException(new FirebaseNoSignedInUserException("There is no user connected!"));
            return tcs.getTask();
        }

        if (firebaseUser.getEmail() == null) {
            tcs.setException(new IllegalStateException("The current user does not have an email address."));
            return tcs.getTask();
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        return firebaseUser.reauthenticate(credential).onSuccessTask(reauthTask ->
                firebaseUser.updateEmail(newEmail).onSuccessTask(updateEmailTask ->
                        firebaseUser.reload().onSuccessTask(reloadUserTask -> {
                            DocumentReference documentReference =
                                    firestore.collection("Users").document(firebaseUser.getUid());
                            Map<String, Object> data = new HashMap<>();
                            data.put("email", newEmail);

                            Task<Void> sendEmail = firebaseUser.sendEmailVerification();
                            Task<Void> updateEmail = documentReference.update(data);

                            return Tasks.whenAll(sendEmail, updateEmail);
                        })));
    }

    /**
     * This method updated the password of the currently connected user. It replaces the old password with the one
     * provided in the parameter {@code oldPassword}.
     *
     * @param oldPassword the current password of the connected user
     * @param newPassword the new password
     * @return the result of the task
     */
    public static Task<Void> updatePassword(String oldPassword, String newPassword) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            tcs.setException(new FirebaseNoSignedInUserException("There is no user connected!"));
            return tcs.getTask();
        }

        if (firebaseUser.getEmail() == null) {
            tcs.setException(new IllegalStateException("The current user does not have an email address."));
            return tcs.getTask();
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);
        return firebaseUser.reauthenticate(credential)
                .onSuccessTask(reauthTask -> firebaseUser.updatePassword(newPassword));
    }

    /**
     * This method deletes the connected user and their data from the Firestore database.
     *
     * @return the result of this task
     */
    public static Task<Void> deleteUser(String password) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            tcs.setException(new FirebaseNoSignedInUserException("There is no user connected!"));
            return tcs.getTask();
        }

        if (firebaseUser.getEmail() == null) {
            tcs.setException(new IllegalStateException("The current user does not have an email address."));
            return tcs.getTask();
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        return firebaseUser.reauthenticate(credential).onSuccessTask(reauthTask ->
                removeUserDataFromFirestore(firebaseUser.getUid()).onSuccessTask(task -> firebaseUser.delete()));
    }

    /**
     * This method logs out the user.
     */
    public static void logout() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
    }

}