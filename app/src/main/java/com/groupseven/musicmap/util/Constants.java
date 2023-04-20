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

    /**
     * Profile user uid argument key used in intent.
     */
    public static final String PROFILE_USER_UID_ARGUMENT = "user_uid";

    /**
     * Author uid argument key used in intent.
     */
    public static final String AUTHOR_UID_ARGUMENT_KEY = "author_uid";

    /**
     * Music memory uid argument key used in intent.
     */
    public static final String MUSIC_MEMORY_UID_ARGUMENT_KEY = "music_memory_uid";

    /**
     * Link to the default user image that is shown if the user doesn't have a profile picture.
     */
    public static final String DEFAULT_USER_IMAGE_URI = "https://i.imgur.com/GvsgVco.jpeg";

    /**
     * Is sent from feed argument key used in intent. {@code true} if sent from feed, {@code false}
     * otherwise.
     */
    public static final String IS_SENT_FROM_FEED_ARGUMENT_KEY = "sent_from_feed";

    /**
     * The pattern to validate usernames.
     */
    public static final Pattern USERNAME_PATTERN = Pattern.compile("\\w{3,25}");

    /**
     * The pattern to validate passwords.
     */
    public static final Pattern PASSWORD_PATTERN = Pattern.compile(".{6,}");

    /**
     * The Spotify client id for our app.
     */
    public static final String SPOTIFY_CLIENT_ID = "56ab7fed83514a7a96a7b735737280d8";

    /**
     * The Spotify redirect uri used in our app.
     */
    public static final String SPOTIFY_REDIRECT_URI = "musicmap://spotify-auth";

    /**
     * The default code verifier for Spotify authentication.
     */
    public static final String SPOTIFY_DEFAULT_CODE_VERIFIER = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";

    /**
     * The query parameter key to receive code from Spotify callback result.
     */
    public static final String SPOTIFY_QUERY_PARAM_KEY = "code";

    /**
     * The refresh token field for Spotify token storage.
     */
    public static final String SPOTIFY_REFRESH_TOKEN_FIELD = "refreshToken";

    /**
     * A comma-separated list of scopes required for our application to interact with Spotify.
     */
    public static final String SPOTIFY_SCOPES = "user-read-currently-playing,user-read-recently-played";

}
