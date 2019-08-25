package com.thuanduong.education.network.Model;


public class News {
    private String title;
    private String link;
    private String smrContents;
    private String img;

    public News(String title, String href, String smrContents, String img){
        this.title = title;
        this.link = href;
        this.smrContents = smrContents;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSmrContents() {
        return smrContents;
    }

    public void setSmrContents(String smrContents) {
        this.smrContents = smrContents;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
