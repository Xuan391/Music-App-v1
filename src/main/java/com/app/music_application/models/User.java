package com.app.music_application.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100,name = "user_name")
    private String userName;
    @Column(nullable = false,name = "password")
    private String password;
    @Column(name = "isAdmin")
    private boolean iaAdmin;
    @Column(name = "name")
    private String name;
    @Column(name = "avatar_url")
    private String avatarUrl;
    // Quan hệ Many-to-Many với chính mình (người dùng follow người dùng)
    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers = new HashSet<>(); //tập hợp các phần tử không trùng lặp
    @Transient
    private int followersCount;

    public int getFollowersCount() {
        return followers.size();
    }

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Song> song;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @JsonIgnore
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists = new ArrayList<>();
    //orphanRemoval = true: Thuộc tính này chỉ định xóa các đối tượng con (Playlist) khi chúng không còn được tham chiếu bởi đối tượng cha (User). Nếu orphanRemoval được đặt thành true, khi một Playlist không còn tham chiếu đến User, nó sẽ tự động bị xóa khỏi cơ sở dữ liệu.

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListenedHistory> listenedHistories = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchHistory> searchHistories = new ArrayList<>();
    public void addDefaultPlaylist() {
        Playlist playlist = new Playlist();
        playlist.setName("Yêu thích");
        playlist.setCreator(this);
        playlist.setFavorite(true);
        playlist.setCreatedAt(LocalDateTime.now());
        this.playlists.add(playlist);
    }
    public User() {
        addDefaultPlaylist();
    }

    public User(Long id, String userName, String password, boolean iaAdmin, String name, String avatarUrl, Set<User> followers, int followersCount, Collection<Song> song, LocalDateTime createdAt, List<Playlist> playlists, List<ListenedHistory> listenedHistories, List<SearchHistory> searchHistories) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.iaAdmin = iaAdmin;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.followers = followers;
        this.followersCount = followersCount;
        this.song = song;
        this.createdAt = createdAt;
        this.playlists = playlists;
        this.listenedHistories = listenedHistories;
        this.searchHistories = searchHistories;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", followers=" + followers +
                ", followersCount=" + followersCount +
                ", createdAt=" + createdAt +
                ", playlists=" + playlists +
                '}';
    }

    public Collection<Song> getSong() {
        return song;
    }

    public void setSong(Collection<Song> song) {
        this.song = song;
    }
}
