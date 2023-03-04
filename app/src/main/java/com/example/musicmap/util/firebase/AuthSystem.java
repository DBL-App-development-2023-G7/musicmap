package com.example.musicmap.util.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.musicmap.user.Artist;
import com.example.musicmap.user.ArtistData;
import com.example.musicmap.user.User;
import com.example.musicmap.user.UserData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
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
     * @param userData the user to be registered
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
                Task<Void> addUser = addUserToFirestore(new User(userData, firebaseUser.getUid()));
                return Tasks.whenAll(setupProfile, sendEmail, addUser);
            }

            TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
            tcs.setException(new NullPointerException("The firebaseUser is null."));
            return tcs.getTask();
        });
    }

    private static UserData getUserData(Map<String, Object> data) throws IllegalArgumentException {

        Object usernameFirebaseString = data.get("username");
        if (!(usernameFirebaseString instanceof String)) {
            throw (new IllegalArgumentException("The username field does not exist or is " +
                    "invalid!"));
        }
        String username = (String) usernameFirebaseString;

        Object firstNameFirebaseString = data.get("firstName");
        if (!(firstNameFirebaseString instanceof String)) {
            throw (new IllegalArgumentException("The first name field does not exist or is " +
                    "invalid!"));
        }
        String firstName = (String) firstNameFirebaseString;

        Object lastNameFirebaseString = data.get("lastName");
        if (!(lastNameFirebaseString instanceof String)) {
            throw (new IllegalArgumentException("The last name field does not exist or is " +
                    "invalid!"));
        }
        String lastName = (String) lastNameFirebaseString;

        Object emailFirebaseString = data.get("email");
        if (!(emailFirebaseString instanceof String)) {
            throw (new IllegalArgumentException("The last name field does not exist or is " +
                    "invalid!"));
        }
        String email = (String) emailFirebaseString;

        Object birthdateFirebaseTimestamp = data.get("birthdate");
        if (!(birthdateFirebaseTimestamp instanceof Timestamp)) {
            throw (new IllegalArgumentException("The birthdate field does not exist or is " +
                    "invalid!"));
        }
        Date birthdate = ((Timestamp) birthdateFirebaseTimestamp).toDate();

        Object artistFirebaseBoolean = data.get("artist");
        if (!(artistFirebaseBoolean instanceof Boolean)) {
            throw (new IllegalArgumentException("The last name field does not exist or is " +
                    "invalid!"));
        }
        boolean artist = (boolean) artistFirebaseBoolean;

        UserData userData = new UserData(username, firstName, lastName, email, birthdate);

        if (artist) {
            userData = getArtistData(data, userData);
        }

        return userData;
    }

    private static ArtistData getArtistData(Map<String, Object> data, UserData userData)
            throws IllegalArgumentException {

        Object verifiedFirebaseBoolean = data.get("verified");
        if (!(verifiedFirebaseBoolean instanceof Boolean)) {
            throw (new IllegalArgumentException("The verified field does not exist or is " +
                    "invalid!"));
        }

        boolean verified = (boolean) verifiedFirebaseBoolean;

        return new ArtistData(userData, verified);
    }

    public static Task<User> getUserFromUid(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Task<DocumentSnapshot> getUserDataFirestore =
                firestore.collection("Users").document(uid).get();

        return getUserDataFirestore.continueWithTask(task -> {
            TaskCompletionSource<User> tcs = new TaskCompletionSource<>();

            if (!task.isSuccessful()) {
                tcs.setException(new Exception("Firestore Exception"));
                return tcs.getTask();
            }

            DocumentSnapshot doc = task.getResult();
            if (!doc.exists()) {
                tcs.setException(new Exception("Firestore Document does not exist!"));
                return tcs.getTask();
            }

            Map<String, Object> data = doc.getData();
            if (data == null) {
                tcs.setException(new Exception("Firestore Document does not have any data!"));
                return tcs.getTask();
            }

            try {
                UserData userData = getUserData(data);
                if (userData instanceof ArtistData) {
                    tcs.setResult(new Artist((ArtistData) userData, uid));
                } else {
                    tcs.setResult(new User(userData, uid));
                }
            } catch (IllegalArgumentException exception) {
                tcs.setException(exception);
            }

            return tcs.getTask();
        });
    }

}
