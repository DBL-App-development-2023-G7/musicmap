package com.example.musicmap.util.regex;

import java.util.regex.Pattern;

public class MMPatterns {

    public static final Pattern USERNAME = Pattern.compile("\\w{3,25}");
    public static final Pattern PASSWORD = Pattern.compile(".{6,}");

}
