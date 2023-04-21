package com.groupseven.musicmap.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class UserTest {

    private UserData data;
    private User user;

    @Before
    public void setUp() {
        data = new UserData("username", "User", "Name", "user@email.com", new Date());
        user = new User(data, "1234");
    }

    @Test
    public void testGetUid() {
        assertEquals("1234", user.getUid());
    }

    @Test
    public void testGetData() {
        assertEquals(data, user.getData());
    }

    @Test
    public void testIsArtist() {
        assertFalse(user.isArtist());

        UserData artistData = new UserData(
                "username",
                "User",
                "Name",
                "user@email.com",
                new Date(),
                true
        );

        User artist = new User(artistData, "1111");
        assertTrue(artist.isArtist());
    }

}
