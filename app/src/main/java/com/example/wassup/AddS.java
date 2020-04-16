package com.example.wassup;

public class AddS {
    public String username,about,uid,image;

    public AddS(){}

    public AddS(String username, String about, String uid, String image) {
        this.username = username;
        this.about = about;
        this.uid = uid;
        this.image = image;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getId() {
        return uid;
    }

    public void setId(String id) {
        this.uid = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
