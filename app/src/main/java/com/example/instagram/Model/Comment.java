package com.example.instagram.Model;

public class Comment {
    private String comment;
    private String publisher;

    // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    public Comment() {
    }

    public Comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    // Getter and Setter methods
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
