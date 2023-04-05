package com.example.musicmap.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class
UserDataTest {

    private UserData userData;

    @Before
    public void setUp() {
        this.userData = new UserData("username", "User",
                "Name", "user@email.com", new Date(), true);
    }

    @Test
    public void testGetUsername() {
        assertEquals(userData.getUsername(), "username");
    }

    @Test
    public void testGetFirstName() {
        assertEquals(userData.getFirstName(), "User");
    }

    @Test
    public void testGetLastName() {
        assertEquals(userData.getLastName(), "Name");
    }

    @Test
    public void testGetEmail() {
        assertEquals(userData.getEmail(), "user@email.com");
    }

    @Test
    public void testGetBirthdate() {
        assertTrue((userData.getBirthdate().getTime() - new Date().getTime()) < 10000);
    }

    @Test
    public void testGetProfilePicture() {
        assertEquals(userData.getProfilePicture(), "");
    }

    @Test
    public void testIsArtist() {
        assertTrue(userData.isArtist());
    }

    @Test
    public void testToUser() {
        User user = this.userData.toUser("uid");
        assertNotNull(user);
        assertEquals(user.getData(), this.userData);
        assertEquals(user.getUid(), "uid");
    }

}
