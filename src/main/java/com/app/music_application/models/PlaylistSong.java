package com.app.music_application.models;

import jakarta.persistence.*;

@Entity
@Table(name = "playlist_song")
public class PlaylistSong {
    @Id
    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    @Id
    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

}
