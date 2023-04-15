package com.groupseven.musicmap.models;

/**
 * This class is used to group song and number of times it was referenced in music memories.
 */
public class SongCount {

    private final Song song;
    private final Long count;

    public SongCount(Song song, Long count) {
        this.song = song;
        this.count = count;
    }

    public Song getSong() {
        return song;
    }

    public long getCount() {
        return count;
    }

}
