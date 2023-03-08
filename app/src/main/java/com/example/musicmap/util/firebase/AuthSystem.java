package com.example.musicmap.util.firebase;

import androidx.annotation.NonNull;

import com.example.musicmap.user.Artist;
import com.example.musicmap.user.ArtistData;
import com.example.musicmap.user.User;
import com.example.musicmap.user.UserData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

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
     * This method retrieves the user that has the given uid and their data.
     *
     * @param uid the given uid of the user
     * @return the result of this task
     */
    public static Task<User> getUserFromUid(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Task<DocumentSnapshot> getUserDataFirestore = firestore.collection("Users").document(uid).get();

        return getUserDataFirestore.onSuccessTask(doc -> {
            TaskCompletionSource<User> tcs = new TaskCompletionSource<>();

            UserData userData = doc.toObject(UserData.class);

            if (!doc.exists()) {
                tcs.setException(new FirebaseFirestoreException("Document does not exist.",
                        FirebaseFirestoreException.Code.NOT_FOUND));
            }

            if (userData != null) {
                if (userData.isArtist()) {
                    ArtistData artistData = doc.toObject(ArtistData.class);
                    tcs.setResult(new Artist(artistData, uid));
                } else {
                    tcs.setResult(new User(userData, uid));
                }
            } else {
                tcs.setException(new FirebaseFirestoreException("An error has occurred while trying to retrieve " +
                        "and apply the user's data from firebase.", FirebaseFirestoreException.Code.UNKNOWN));
            }

            return tcs.getTask();
        });
    }

    /**
     * This method retrieves the connected user and their data.
     *
     * @return the result of this task
     */
    public static Task<User> getUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            TaskCompletionSource<User> tcs = new TaskCompletionSource<>();
            tcs.setException(new FirebaseNoSignedInUserException("There is no user connected!"));
            return tcs.getTask();
        }

        return getUserFromUid(firebaseUser.getUid());
    }

    /**
     * This method remove the data stored in the Firestore database of the user that has the given uid. This method
     * is intentionally made private.
     *
     * @param uid the uid of the user
     * @return the result of this task
     */
    private static Task<Void> removeUserFromFirestore(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Users").document(uid).delete();
    }

    /**
     * This method deletes the connected user and their data from the Firestore database.
     *
     * @return the result of this task
     */
    public static Task<Void> deleteUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        if (firebaseUser == null) {
            tcs.setException(new FirebaseNoSignedInUserException("There is no user connected!"));
            return tcs.getTask();
        }

        return removeUserFromFirestore(firebaseUser.getUid()).onSuccessTask(task -> firebaseUser.delete());
    }

}