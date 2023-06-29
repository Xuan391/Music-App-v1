package com.app.music_application.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "listened_History")
public class ListenedHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "songId")

    private Song songId;

    @Column(name = "listened_time")
    private LocalDateTime listenedTime;

    public ListenedHistory() {}

    public ListenedHistory(User userId, Song songId, LocalDateTime listenedTime) {
        this.userId = userId;
        this.songId = songId;
        this.listenedTime = listenedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Song getSongId() {
        return songId;
    }

    public void setSongId(Song songId) {
        this.songId = songId;
    }

    public LocalDateTime getListenedTime() {
        return listenedTime;
    }

    public void setListenedTime(LocalDateTime listenedTime) {
        this.listenedTime = listenedTime;
    }

    @Override
    public String toString() {
        return "ListenedHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", songId=" + songId +
                ", listenedTime=" + listenedTime +
                '}';
    }
}
