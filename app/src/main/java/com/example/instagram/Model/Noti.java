package com.example.instagram.Model;

public class Noti {
    private String userID;
    private String content;
    private String postId;
    private Boolean isPost;


    public Noti() {
    }

    public Noti(String userID, String content, String postId, Boolean isPost) {
        this.userID = userID;
        this.content = content;
        this.postId = postId;
        this.isPost = isPost;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

