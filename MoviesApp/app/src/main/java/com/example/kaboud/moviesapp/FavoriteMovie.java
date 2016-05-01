package com.example.kaboud.moviesapp;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kaboud on 4/8/2016.
 */
public class FavoriteMovie extends RealmObject {

    @PrimaryKey
    private int ID;
    private String Title;
    private String Overview;
    private String PosterURL;
    private String ReleaseDate;
    private String Rate;


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getOverview() {
        return Overview;
    }

    public void setOverview(String overview) {
        Overview = overview;
    }

    public String getPosterURL() {
        return PosterURL;
    }

    public void setPosterURL(String posterURL) {
        PosterURL = posterURL;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        ReleaseDate = releaseDate;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }
}
