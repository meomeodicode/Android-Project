package com.example.instagram.Model;

public class UserModel {
    private String id;
    private String userName;
    private String fullName;
    private String imageUrl;
    private String bio;

    public UserModel(String id, String username, String fullname, String avatar, String bio) {
        this.id = id;
        this.userName = username;
        this.fullName = fullname;
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
    public String getFullname() {
        return fullName;
    }
    public void setFullname(String fullname) {
        this.fullName = fullname;
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
