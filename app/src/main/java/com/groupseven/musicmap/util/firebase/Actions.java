package com.groupseven.musicmap.util.firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.groupseven.musicmap.models.MusicMemory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.groupseven.musicmap.util.TaskUtil;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        StorageReference rootReference = FirebaseStorage.getInstance().getReference();
        String uuid = UUID.randomUUID().toString();
        StorageReference imageRef = rootReference.child(String.format("users/%s/memories/%s.jpg", authorUid, uuid));
        UploadTask uploadTask = imageRef.putBytes(data);

        return TaskUtil.getFuture(uploadTask)
                .thenCompose(unused -> TaskUtil.getFuture(imageRef.getDownloadUrl()))
                .whenComplete((url, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Could not upload the image", throwable);
                    }
                });
    }

}
