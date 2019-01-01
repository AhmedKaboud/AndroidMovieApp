package com.example.kaboud.moviesapp;

import java.io.Serializable;

/**
 * Created by Kaboud on 4/12/2016.
 */
public class MovieTrailer implements Serializable {
    private String Id;
    private String Key;
    private String Name;
    private String Site;

    public MovieTrailer() {}

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSite() {
        return Site;
    }

    public void setSite(String site) {
        Site = site;
    }
}
