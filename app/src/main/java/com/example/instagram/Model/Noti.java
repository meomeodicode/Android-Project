package com.example.instagram.Model;

public class Noti {
    private String userID;
    private String description;
    private String postId;
    private Boolean isPost;


    public Noti() {
    }

    public Noti(String userID, String content, String postID, Boolean isPost) {
        this.userID = userID;
        this.description = content;
        this.postId = postID;
        this.isPost = isPost;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Boolean getIsPost() {
        return isPost;
    }

    public void setIsPost(Boolean isPost) {
        this.isPost = isPost;
    }
}

