package com.atbe.abe.topmovieslist;

import android.graphics.Bitmap;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by abe on 12/24/16.
 */

public class MovieContainer {

    private MovieDb mMovie = null;
    private Bitmap mPosterImage = null;
    private String mTrailerUrl = null;

    public MovieContainer(MovieDb movie) {
        mMovie = movie;
    }

    public void setmPosterImage(Bitmap mPosterImage) {
        this.mPosterImage = mPosterImage;
    }

    public void setmTrailerUrl(String mTrailerUrl) {
        this.mTrailerUrl = mTrailerUrl;
    }

    public MovieDb getmMovie() {
        return mMovie;
    }

    public Bitmap getmPosterImage() {
        return mPosterImage;
    }

    public String getmTrailerUrl() {
        return mTrailerUrl;
    }

}
