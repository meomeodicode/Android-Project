package com.example.instagram.post;

public class Post {
    private String username;
    private String description;
    private int profileImageResource;
    private int postImageResource;

    public Post(String username, String description, int profileImageResource, int postImageResource) {
        this.username = username;
        this.description = description;
        this.profileImageResource = profileImageResource;
        this.postImageResource = postImageResource;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public int getProfileImageResource() {
        return profileImageResource;
    }

    public int getPostImageResource() {
        return postImageResource;
    }
}
