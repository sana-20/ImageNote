package com.line.imagenote.models;

import java.util.ArrayList;

public class NoteItem {
    private int id;
    private String title;
    private String content;
    private String time;

    private ArrayList<String> photoList;

    public NoteItem(int id, String title, String content, String time, ArrayList<String> photoList) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.photoList = photoList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<String> photoList) {
        this.photoList = photoList;
    }
}
