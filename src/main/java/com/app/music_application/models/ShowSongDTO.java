package com.app.music_application.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowSongDTO {
    private Long songId;
    private String url;
    private String thumbnail;
    private String nameSong;
    private Long userId;
    private String nameUser;

    public ShowSongDTO() {
    }

    public ShowSongDTO(Long songId, String url, String thumbnail, String nameSong, Long userId, String nameUser) {
        this.songId = songId;
        this.url = url;
        this.thumbnail = thumbnail;
        this.nameSong = nameSong;
        this.userId = userId;
        this.nameUser = nameUser;
    }
}
