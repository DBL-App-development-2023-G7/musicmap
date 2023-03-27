package com.example.musicmap.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class ArtistDataTest {

    private ArtistData artistData;

    @Before
    public void setUp() {
        UserData userData = new UserData("username", "User",
                "Name", "user@email.com", new Date(), true);
        this.artistData = new ArtistData(userData, true);

    }

    @Test
    public void testGetVerified() {
        assertTrue(this.artistData.getVerified());
    }

    @Test
    public void testToUser() {
        User user = this.artistData.toUser("uid");
        assertNotNull(user);
        assertEquals(user.getUid(), "uid");
        assertEquals(user.getData(), this.artistData);
    }

}
