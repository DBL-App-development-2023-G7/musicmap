package com.example.musicmap.user;

import junit.framework.TestCase;

import java.util.Date;

public class ArtistDataTest extends TestCase {

    private ArtistData artistData;

    public void setUp() throws Exception {
        super.setUp();
        UserData userData = new UserData("username", "User",
                "Name", "user@email.com", new Date(), true);
        this.artistData = new ArtistData(userData, true);

    }

    public void testGetVerified() {
        assertTrue(this.artistData.getVerified());
    }

    public void testToUser() {
        User user = this.artistData.toUser("uid");
        assertNotNull(user);
        assertEquals(user.getUid(), "uid");
        assertEquals(user.getData(), this.artistData);
    }

}
