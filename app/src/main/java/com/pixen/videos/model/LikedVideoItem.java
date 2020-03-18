package com.pixen.videos.model;

public class LikedVideoItem {

    private String video_id;
    private String action;
    private String user_id;

    public LikedVideoItem() {
    }

    public LikedVideoItem(String video_id, String action, String user_id) {
        this.video_id = video_id;
        this.action = action;
        this.user_id = user_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
