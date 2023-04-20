package com.groupseven.musicmap.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class PostTest {

    private Post post;
    private String authorUid;
    private Date timePosted;
    private GeoPoint location;

    @Before
    public void setUp() {
        authorUid = "1234567890";
        timePosted = new Date();
        location = new GeoPoint(37.7749, -122.4194);
        post = new PostImpl(authorUid, timePosted, location);
    }

    @Test
    public void testGetUid() {
        assertNull(post.getUid());
    }

    @Test
    public void testGetAuthorUid() {
        assertEquals(authorUid, post.getAuthorUid());
    }

    @Test
    public void testGetTimePosted() {
        assertEquals(timePosted, post.getTimePosted());
    }

    @Test
    public void testGetLocation() {
        assertEquals(location, post.getLocation());
    }

    @Test
    public void testSetAuthorUid() {
        assertThrows(IllegalStateException.class, () -> {
            post.setAuthorUid("newAuthorUid");
        });
    }

    private static class PostImpl extends Post {

        public PostImpl(String authorUid, Date timePosted, GeoPoint location) {
            super(authorUid, timePosted, location);
        }

    }

}