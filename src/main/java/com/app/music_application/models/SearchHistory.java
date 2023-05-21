package com.app.music_application.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name = "search_query")
    private String searchQuery;

    @Column(name = "search_time")
    private LocalDateTime searchTime;
    // Các constructors
    public SearchHistory() {}

    public SearchHistory(User userId, String searchQuery, LocalDateTime searchTime) {
        this.userId = userId;
        this.searchQuery = searchQuery;
        this.searchTime = searchTime;
    }

    // Các getter và setter


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

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public LocalDateTime getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(LocalDateTime searchTime) {
        this.searchTime = searchTime;
    }

    @Override
    public String toString() {
        return "SearchHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", searchQuery='" + searchQuery + '\'' +
                ", searchTime=" + searchTime +
                '}';
    }
}

