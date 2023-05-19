package com.app.music_application.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "playlist_id")
    private List<Playlist> playlists = new ArrayList<>();
    //orphanRemoval = true: Thuộc tính này chỉ định xóa các đối tượng con (Playlist) khi chúng không còn được tham chiếu bởi đối tượng cha (User). Nếu orphanRemoval được đặt thành true, khi một Playlist không còn tham chiếu đến User, nó sẽ tự động bị xóa khỏi cơ sở dữ liệu.

    public void addDefaultPlaylist() {
        Playlist playlist = new Playlist();
        playlist.setName("Yêu thích");
        playlist.setCreatorId(this);
        playlist.setFavorite(true);
        playlist.setCreatedAt(LocalDateTime.now());
        this.playlists.add(playlist);
    }
    public User() {
        addDefaultPlaylist();
    }

    public User(String userName, String password, String name, String avatarUrl, Set<User> followers, int followersCount, LocalDateTime createdAt, List<Playlist> playlists) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.followers = followers;
        this.followersCount = followersCount;
        this.createdAt = createdAt;
        this.playlists = playlists;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
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
}
