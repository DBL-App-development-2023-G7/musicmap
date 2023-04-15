package com.groupseven.musicmap.util.firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.util.TaskUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Actions for interacting with the Firebase server.
 */
public class Actions {

    private static final String TAG = "Actions";

    /**
     * Posts the given music memory to the database.
     *
     * @param musicMemory a future indicating when/if the music memory is posted.
     */
    public static CompletableFuture<?> postMusicMemory(MusicMemory musicMemory) {
        String authorUid = musicMemory.getAuthorUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return TaskUtil.getFuture(firestore.collection("Users").document(authorUid)
                        .collection("MusicMemories").add(musicMemory))
                .whenComplete((documentReference, throwable) -> {
                    if (throwable == null) {
                        Log.i(TAG, "Successfully created music memory with ID " + documentReference.getId());
                    } else {
                        Log.e(TAG, "Could not create music memory", throwable);
                    }
                });
    }

    /**
     * Uploads music memory image for the user.
     *
     * @param capturedImage the bitmap of the image to upload.
     * @param authorUid the id of the author of the image.
     * @return a future containing a {@link Uri} for downloading the image.
     */
    public static CompletableFuture<Uri> uploadMusicMemoryImage(Bitmap capturedImage, String authorUid) {
        // Convert the image to JPEG
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        // Store the JPEG image under the author UID with a random UUID
        StorageReference rootReference = FirebaseStorage.getInstance().getReference();

        // Run the whole thing on a separate thread, so we can simply wait for a task to be done
        return CompletableFuture.supplyAsync(() -> {
            // Generate a path in the storage that does not exist already (avoid collisions)
            StorageReference imageRef;
            boolean pathExists;
            do {
                // Generate a random UUID and form the storage path with it
                String uuid = UUID.randomUUID().toString();
                imageRef = rootReference.child(String.format("users/%s/memories/%s.jpg", authorUid, uuid));

                Log.d(TAG, "Checking if image '" + imageRef.getPath() + "' exists");

                /*
                Firebase storage doesn't have a way to check if a StorageReference exists, see
                https://github.com/flutter/flutter/issues/18315

                Therefore, try to get the download url and catch the exception if the path doesn't exist.

                This will still print an exception to console each time it finds a path that doesn't exist,
                but at least it avoids collisions.
                 */
                try {
                    TaskUtil.getFuture(imageRef.getDownloadUrl()).join();

                    pathExists = true;
                } catch (CompletionException e) {
                    // If we get an exception other than the expected one, we should still fail
                    if (!(e.getCause() instanceof IOException)) {
                        throw e;
                    }

                    Log.w(TAG, "You can ignore the exception above, this is expected behavior; "
                            + "see Actions#uploadMusicMemoryImage");
                    pathExists = false;
                }
            } while (pathExists);

            // Start uploading and get download url
            Log.d(TAG, "Started uploading image (" + data.length + " bytes)");
            TaskUtil.getFuture(imageRef.putBytes(data)).join();
            Log.d(TAG, "Uploading image");

            return TaskUtil.getFuture(imageRef.getDownloadUrl()).join();
        });
    }

}
