package com.line.imagenote.models;


public class Note {
    private Long timeCreated;
    private Long timeModified;
    private String title;
    private String content;

    public Note(Long timeCreated, Long timeModified, String title, String content) {
        this.timeCreated = timeCreated;
        this.timeModified = timeModified;
        this.title = title;
        this.content = content;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Long timeModified) {
        this.timeModified = timeModified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
