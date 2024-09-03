package com.example.instagram.post;

public class Post {
    private String postId;
    private String publisher;
    private String description;
    private String postImage;
    private long timestamp;

    public Post(String postId, String publisher, String description, String postImage, long timestamp) {
        this.postId = postId;
        this.publisher = publisher;
        this.description = description;
        this.postImage = postImage;
        this.timestamp = timestamp;
    }

    public Post() {}

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
