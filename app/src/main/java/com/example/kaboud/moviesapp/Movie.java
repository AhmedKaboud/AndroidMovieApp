package com.example.kaboud.moviesapp;

import java.io.Serializable;

/**
 * Created by Kaboud on 3/27/2016.
 */
public class Movie implements Serializable {

    private int ID;
    private String Title;
    private String Overview;
    private String PosterURL;
    private String ReleaseDate;
    private String Rate;
    private MovieTrailer[] MovieTrailerArr;
    private MovieReview[] MovieReviewArr;

    public MovieReview[] getMovieReviewArr() {
        return MovieReviewArr;
    }

    public void setMovieReviewArr(MovieReview[] movieReviewArr) {
        MovieReviewArr = movieReviewArr;
    }

    public MovieTrailer[] getMovieTrailerArr() {
        return MovieTrailerArr;
    }

    public void setMovieTrailerArr(MovieTrailer[] movieTrailerArr) {
        MovieTrailerArr = movieTrailerArr;
    }

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
