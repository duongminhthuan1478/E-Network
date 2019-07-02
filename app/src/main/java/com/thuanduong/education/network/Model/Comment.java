package com.thuanduong.education.network.Model;

public class Comment {

    String comment, date, time, username, profileimage, uid;

    public Comment(){}

    public Comment(String comment, String date, String time, String username, String profileimage,
            String uid) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.username = username;
        this.profileimage = profileimage;
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
