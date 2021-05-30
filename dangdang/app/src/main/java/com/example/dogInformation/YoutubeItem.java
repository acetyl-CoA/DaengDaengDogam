package com.example.dogInformation;

import android.graphics.Bitmap;

public class YoutubeItem {
    String videoID = "";
    String title = "";
    String description = "";
    Bitmap thumbnail = null;
    String link = "";

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoID() {
        return videoID;
    }
}
