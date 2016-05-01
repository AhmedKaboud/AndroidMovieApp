package com.example.kaboud.moviesapp;

import java.io.Serializable;

/**
 * Created by Kaboud on 4/12/2016.
 */
public class MovieReview implements Serializable {
    private String Id;
    private String Auther;
    private String Content;
    private String URL;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAuther() {
        return Auther;
    }

    public void setAuther(String auther) {
        Auther = auther;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
