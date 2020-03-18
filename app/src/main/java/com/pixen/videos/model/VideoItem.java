package com.pixen.videos.model;

public class VideoItem {

    private String video_url;
    private String views;
    private String likes;
    private long time;
    private String video_id;

    public VideoItem() {
    }

    public VideoItem(String video_url, String views, String likes, long time, String video_id) {
        this.video_url = video_url;
        this.views = views;
        this.likes = likes;
        this.time = time;
        this.video_id = video_id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }
}
