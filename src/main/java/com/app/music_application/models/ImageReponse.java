package com.app.music_application.models;

public class ImageReponse {
    private String path;
    private String desc;
    public ImageReponse() {}

    public ImageReponse(String path, String desc) {
        this.path = path;
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
