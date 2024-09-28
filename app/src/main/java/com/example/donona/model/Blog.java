package com.example.donona.model;

public class Blog {
    private String thumbnail = "";
    private String content = "";
    private String title = "";
    private String author = "";
    private String authorProfile = "";
    private String thumbnailContent = "";

    public Blog() {}

    public Blog(String thumbnail, String content, String title, String thumbnailContent) {
        this.thumbnail = thumbnail;
        this.content = content;
        this.title = title;
        this.thumbnailContent = thumbnailContent;
    }

    public String getBlogContent() { return this.content; }

    public String getTitle() { return this.title; }

    public String getThumbnail() { return this.thumbnail; }

    public String getThumbnailContent() { return this.thumbnailContent; }

}
