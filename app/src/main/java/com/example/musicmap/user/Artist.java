package com.example.musicmap.user;

import java.util.Date;
import java.util.Map;

public class Artist extends User {
    public Artist(User user) {
        super(user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getBirthdate(), user.getUuid());

    }

    public Artist(String username, String firstName, String lastName, String email,
                  Date birthdate, String uuid) {
        super(username, firstName, lastName, email, birthdate, uuid);

    }

    @Override
    public Map<String, Object> getFirestoreAttributes() {
        Map<String, Object> attributes = super.getFirestoreAttributes();
        attributes.put("artist", true);
        return attributes;
    }
}
