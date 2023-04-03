package com.example.musicmap.util.user;

import com.example.musicmap.user.UserData;

import java.util.Date;

public class UserDataTestStore {

    public static UserData getValidUserData() {
        return new UserData("username", "User", "Name", "user@example.com", new Date());
    }
}
