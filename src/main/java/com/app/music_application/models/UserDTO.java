package com.app.music_application.models;

public class UserDTO {
    private Long userId;
    private String name;
    private String username;
    private String password;
    private String avatarURL;
    private int followerCount;

    public UserDTO() {}

    public UserDTO( String name, String username, String password, String avatarURL, int followerCount) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.avatarURL = avatarURL;
        this.followerCount = followerCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }
}
