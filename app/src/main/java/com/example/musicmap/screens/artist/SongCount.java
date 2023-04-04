package com.example.musicmap.screens.artist;

import com.example.musicmap.feed.Song;

/**
 * This class is used to group song and number of times it was referenced in music memories.
 */
public class SongCount {

    private Song song;
    private Long count;

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
