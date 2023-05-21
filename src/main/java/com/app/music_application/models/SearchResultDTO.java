package com.app.music_application.models;

public class SearchResultDTO {
    private String type;
    private Long id;
    private String name;

    // Constructors, getters, and setters

    public SearchResultDTO() {
    }

    public SearchResultDTO(String type, Long id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
