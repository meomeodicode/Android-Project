package com.example.instagram.post;

public class Post {
    private String postId;
    private String publisher;
    private String description;
    private String postImage;

    public Post(String postId, String publisher, String description, String postImageResource) {
        this.postId = postId;
        this.publisher = publisher;
        this.description = description;
        this.postImage = postImage;
    }
    public Post(){};

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}