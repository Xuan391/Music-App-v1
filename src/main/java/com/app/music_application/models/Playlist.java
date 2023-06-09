package com.app.music_application.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "playlist")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonIdentityReference(alwaysAsId = true)
    private User creator;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "is_favorite")
    private boolean isFavorite;
    @ManyToMany
    @JoinTable(
            name = "playlist_song",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Song> songs = new HashSet<>();

    public Playlist(){}

    public Playlist(String name, User creatorId, LocalDateTime createdAt, Set<Song> songs) {
        this.name = name;
        this.creator = creatorId;
        this.createdAt = createdAt;
        this.songs = songs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creatorId) {
        this.creator = creatorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creatorId=" + creator +
                ", createdAt=" + createdAt +
                ", isFavorite=" + isFavorite +
                ", songs=" + songs +
                '}';
    }
}
