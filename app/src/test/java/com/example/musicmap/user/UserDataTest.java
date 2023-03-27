package com.example.musicmap.user;

import junit.framework.TestCase;

import java.util.Date;

public class UserDataTest extends TestCase {
    private UserData userData;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.userData = new UserData("username", "User",
                "Name", "user@email.com", new Date(), true);
    }

    public void testGetUsername() {
        assertEquals(userData.getUsername(), "username");
    }

    public void testGetFirstName() {
        assertEquals(userData.getFirstName(), "User");
    }

    public void testGetLastName() {
        assertEquals(userData.getLastName(), "Name");
    }

    public void testGetEmail() {
        assertEquals(userData.getEmail(), "user@email.com");
    }

    public void testGetBirthdate() {
        assertTrue((userData.getBirthdate().getTime() - new Date().getTime()) < 10000);
    }

    public void testGetProfilePicture() {
        assertEquals(userData.getProfilePicture(), "");
    }

    public void testHasProfilePicture() {
        assertFalse(userData.hasProfilePicture());
    }

    public void testIsArtist() {
        assertTrue(userData.isArtist());
    }

    public void testToUser() {
        User user = this.userData.toUser("uid");
        assertNotNull(user);
        assertEquals(user.getData(), this.userData);
        assertEquals(user.getUid(), "uid");
    }
}