package com.line.imagenote.models;


public class Attachment {
    private int photoId;
    private String uri;

    public Attachment(int photoId, String uri) {
        this.photoId = photoId;
        this.uri = uri;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
