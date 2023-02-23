package com.example.musicmap.utils;


import java.util.regex.Pattern;

public class MMPatterns {

    public static final String USERNAME_STR = "^[a-zA-Z][\\\\w_]{7,29}$";
    public static final Pattern USERNAME = Pattern.compile(USERNAME_STR);
}
