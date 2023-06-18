package com.app.music_application.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "song")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 300,name = "name")
    private String name;
    // Một bài hát thuộc về một Category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(nullable = false,unique = true,name = "url")
    private String url;
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creatorId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "download_count")
    private int downloadCount;
    @Column(name = "listened_count")
    private int listenedCount;
    @Column(name = "singer")
    private String singer;

    public Song() {}

    public Song(Long id, String name, Category category, String url, String thumbnailUrl, User creatorId, LocalDateTime createdAt, int downloadCount, int listenedCount, String singer) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.downloadCount = downloadCount;
        this.listenedCount = listenedCount;
        this.singer = singer;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public User getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(User creatorId) {
        this.creatorId = creatorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public int getListenedCount() {
        return listenedCount;
    }

    public void setListenedCount(int listenedCount) {
        this.listenedCount = listenedCount;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", creatorId=" + creatorId +
                ", createdAt=" + createdAt +
                ", downloadCount=" + downloadCount +
                ", listenedCount=" + listenedCount +
                '}';
    }
}
