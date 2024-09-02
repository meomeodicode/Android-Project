package com.example.instagram.Model;

public class UserModel {
    private String id;
    private String username;
    private String email;
    private String imageUrl;
    private String bio;

    // Default constructor (required for calls to DataSnapshot.getValue(UserModel.class))
    public UserModel() {
    }

    // Constructor with parameters
    public UserModel(String id, String username, String email, String imageUrl, String bio) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        this.bio = bio;
    }

    // Getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
}