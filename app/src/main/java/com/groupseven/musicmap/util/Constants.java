package com.groupseven.musicmap.util;

import java.util.regex.Pattern;

/**
 * This class defines constants used in the application.
 */
public final class Constants {

    /**
     * Broadcast action name for internet check.
     */
    public static final String INTERNET_BROADCAST_ACTION = "internet_check";

    /**
     * Internet broadcast intent bundle key.
     */
    public static final String INTERNET_BROADCAST_BUNDLE_KEY = "available";

    public static final String PROFILE_USER_UID_ARGUMENT = "user_uid";

    public static final String AUTHOR_UID_ARGUMENT_KEY = "author_uid";

    public static final String MUSIC_MEMORY_UID_ARGUMENT_KEY = "music_memory_uid";

    public static final String DEFAULT_USER_IMAGE_URI = "https://i.imgur.com/GvsgVco.jpeg";

    public static final String IS_SENT_FROM_FEED_ARGUMENT_KEY = "sent_from_feed";

    public static final Pattern USERNAME_PATTERN = Pattern.compile("\\w{3,25}");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile(".{6,}");

    public static final String SPOTIFY_CLIENT_ID = "56ab7fed83514a7a96a7b735737280d8";

    public static final String SPOTIFY_REDIRECT_URI = "musicmap://spotify-auth";

}
