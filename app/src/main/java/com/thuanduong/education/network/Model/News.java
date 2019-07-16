package com.thuanduong.education.network.Model;

import lombok.Data;

@Data
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

}
