package com.example.musicmap.user;

import java.util.UUID;

public class Artist extends User {
    public Artist(User user) {
        super(user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getUuid());

    }

    public Artist(String username, String firstName, String lastName, String email, UUID uuid) {
        super(username, firstName, lastName, email, uuid);

    }
}
