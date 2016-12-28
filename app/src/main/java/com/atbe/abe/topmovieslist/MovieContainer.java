package com.atbe.abe.topmovieslist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.SparseArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by abe on 12/24/16.
 */

public class MovieContainer {

    private MovieDb mMovie = null;
    private Bitmap mPosterImage = null;
    private String mTrailerUrl = null;

    private Activity mActivity = null;
    private MovieRecyclerAdapter mAdapter = null;

    public MovieContainer(MovieDb movie, Activity activity, MovieRecyclerAdapter adapter) {
        mMovie = movie;
        mActivity = activity;
        mAdapter = adapter;

        new FetchPosterImageTask().execute(mMovie);
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

    private class FetchPosterImageTask extends AsyncTask<MovieDb, Void, Bitmap> {

        /** Helper which downloads the image file.
         * @param uri The URL the image is housed at.
         * @return The image or null if there was some error.
         */
        private Bitmap GetImage(String uri) {
            Bitmap image = null;
            try{
                InputStream in = new java.net.URL(uri).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                System.out.println("Error: GetImage uri invalid: " + uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        /** Initiates the network call and retrieves the image for the movie.
         */
        protected Bitmap doInBackground(MovieDb... movies) {
            TmdbApi api = new TmdbApi(mActivity.getString(R.string.rotten_api_key));

            String imageBaseUrl = api.getConfiguration().getSecureBaseUrl();
            String imageSizeParam = "w154";
            SparseArray<Bitmap> images = new SparseArray<Bitmap>();

            MovieDb movie = movies[0];
            String posterUri = imageBaseUrl + imageSizeParam + movie.getPosterPath();
            Bitmap image = GetImage(posterUri);
            images.append(movie.getId(), image);

            if (image == null) {
                System.out.println("ERROR: MovieContainer[FetchPosterImageTask](doInBackground) - " +
                        "image is null");
            }

            return image;
        }

        @Override
        /** When we receive the image, we need to update the data set for the adapter.
         *
         * @param results The movies that were returned.
         */
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);

            setmPosterImage(image);

            // Notify the adapter of the changes
            mAdapter.notifyDataSetChanged();
        }
    }

}
