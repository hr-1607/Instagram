package com.harshit.instagram.Model;

public class Comment {
    String comment;
    String publisher;

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

    public Comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }
    public Comment(){}
}
