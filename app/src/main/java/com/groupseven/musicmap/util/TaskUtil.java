package com.groupseven.musicmap.util;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * Utility class for converting between {@link Task} and {@link CompletableFuture}.
 */
public class TaskUtil {

    /**
     * Converts the given {@link Task} to a {@link CompletableFuture}.
     *
     * The returned future will complete when the task does so, either
     * by completing successfully, by failing through an exception or by being cancelled.
     *
     * @param inputTask the task to convert.
     * @return the future.
     * @param <T> the type of the task, and the type of the returned future.
     */
    public static <T> CompletableFuture<T> getFuture(Task<T> inputTask) {
        CompletableFuture<T> future = new CompletableFuture<>();
        inputTask.addOnCompleteListener(Runnable::run, task -> {
            if (task.isSuccessful()) {
                future.complete(task.getResult());
            } else {
                Exception exception = task.getException();
                if (exception == null) {
                    exception = new RuntimeException("Task completed with unknown exception");
                }

                future.completeExceptionally(exception);
            }
        });
        return future;
    }

    /**
     * Converts the given {@link CompletableFuture} to a {@link Task}.
     *
     * @param future the future to convert.
     * @return the task.
     * @param <T> the type of the future, and the type of the returned task.
     */
    public static <T> Task<T> getTask(CompletableFuture<T> future) {
        TaskCompletionSource<T> tcs = new TaskCompletionSource<>();

        future.handle((BiFunction<T, Throwable, Void>) (t, throwable) -> {
            if (throwable != null) {
                Exception exception;
                if (throwable instanceof Exception) {
                    exception = (Exception) throwable;
                } else {
                    exception = new RuntimeException(
                            "Non-Exception throwable propagated through RuntimeException", throwable);
                }

                tcs.setException(exception);
            } else if (t != null) {
                tcs.setResult(t);
            } else {
                throw new IllegalStateException("Handle called without exception or result");
            }

            return null;
        });

        return tcs.getTask();
    }

}
