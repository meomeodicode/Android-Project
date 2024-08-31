package com.example.instagram.Model;

public class UserModel {
    private String id;
    private String userName;
    private String email;
    private String imageUrl;
    private String bio;

    public UserModel(String id, String username, String fullname, String avatar, String bio) {
        this.id = id;
        this.userName = username;
        this.email = fullname;
        this.imageUrl = avatar;
        this.bio = bio;
    }

    public UserModel() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String fullname) {
        this.email = fullname;
    }
    public String getImageurl() {
        return imageUrl;
    }
    public void setImageurl(String imageurl) {
        this.imageUrl = imageurl;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

}
