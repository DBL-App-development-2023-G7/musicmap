package com.groupseven.musicmap.util;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import org.junit.Test;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TaskUtilTest {

    @Test
    public void testGetFuture_success() throws ExecutionException, InterruptedException {
        Task<String> task = Tasks.forResult("hello");
        CompletableFuture<String> future = TaskUtil.getFuture(task);
        assertNotNull(future);
        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("hello", future.get());
    }

    @Test
    public void testGetFuture_failure() throws InterruptedException {
        Task<String> task = Tasks.forException(new RuntimeException("Task failed"));
        CompletableFuture<String> future = TaskUtil.getFuture(task);
        assertNotNull(future);
        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
        try {
            future.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof RuntimeException);
            assertEquals("Task failed", e.getCause().getMessage());
        }
    }

    @Test
    public void testGetTask_success() {
        CompletableFuture<String> future = CompletableFuture.completedFuture("hello");
        Task<String> task = TaskUtil.getTask(future);
        assertNotNull(task);
        assertTrue(task.isSuccessful());
        assertEquals("hello", task.getResult());
    }

    @Test
    public void testGetTask_failure() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Future failed"));
        Task<String> task = TaskUtil.getTask(future);
        assertNotNull(task);
        assertTrue(task.isComplete());
        assertFalse(task.isSuccessful());
        try {
            task.getResult();
        } catch (RuntimeException e) {
            assertNotNull(e.getCause());
            assertEquals(e.getCause().getMessage(), "Future failed");
        }
    }

}

