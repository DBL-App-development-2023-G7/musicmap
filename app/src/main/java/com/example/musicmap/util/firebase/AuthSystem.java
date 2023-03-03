package com.example.musicmap.util.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.musicmap.user.User;
import com.example.musicmap.user.UserData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AuthSystem {

    /**
     * This method updates the profile(the display name of that account and the link to the
     * profile picture) of the given use ({@code Firebase User}).
     *
     * @param firebaseUser the given Firebase User
     * @param displayName  the changed display name of the user
     * @param photoUri     the changed profile photo uri of the user
     * @return the result of this tasks
     */
    public static Task<Void> updateUserProfile(@NonNull FirebaseUser firebaseUser,
                                               String displayName, String photoUri) {
        UserProfileChangeRequest request =
                new UserProfileChangeRequest.Builder().setDisplayName(displayName)
                        .setPhotoUri(Uri.parse(photoUri)).build();

        return firebaseUser.updateProfile(request);
    }

    /**
     * This method adds the user and its attributes to the Firebase Firestore database.
     *
     * @param user the user to add to the Firestore database
     */
    public static Task<Void> addUserToFirestore(@NonNull User user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> data = user.getData().getFirestoreAttributes();
        return firestore.collection("Users").document(user.getUid()).set(data);
    }

    /**
     * This method registers a user using the Firebase Auth system. This method also add the date
     * that is not contained in the profile of the user to the Firestore Database and sends a
     * verification email to the given user's email address.
     *
     * @param userData     the user to be registered
     * @param password the password of the user to be registered
     * @return the result of this task
     */
    public static Task<Void> register(UserData userData, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = userData.getEmail();

        Task<AuthResult> registerAccount = auth.createUserWithEmailAndPassword(email, password);
        return registerAccount.continueWithTask(task -> {
            FirebaseUser firebaseUser = task.getResult().getUser();

            if (firebaseUser != null) {
                Task<Void> setupProfile = updateUserProfile(firebaseUser,
                        userData.getFirstName() + " " + userData.getLastName(), "");
                Task<Void> sendEmail = firebaseUser.sendEmailVerification();
                Task<Void> addUser = addUserToFirestore(new User (userData, firebaseUser.getUid()));
                return Tasks.whenAll(setupProfile, sendEmail, addUser);
            }

            TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
            tcs.setException(new NullPointerException("The firebaseUser is null."));
            return tcs.getTask();
        });
    }

}
