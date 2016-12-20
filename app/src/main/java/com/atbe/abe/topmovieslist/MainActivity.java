package com.atbe.abe.topmovieslist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MainActivity extends FragmentActivity {
    // the number of movies you want to get in a single request to their web server
    private static final int MOVIE_PAGE_LIMIT = 10;

    // Movies themselves
    private ArrayList<MovieDb> movieItems = new ArrayList<MovieDb>();

    // Image map for the movies
    public static SparseArray<Bitmap> imageUrls = new SparseArray<Bitmap>();

    // The adapter with the movies
    ArrayAdapter theAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Movie list adapter needs the movies and the image map
        theAdapter = new MovieListItemAdapter(this, movieItems);

        // Grab the ListView and attach the adapter
        ListView moviesList = (ListView) findViewById(R.id.movie_listview);
        moviesList.setAdapter(theAdapter);
    }

    /** This handler will go fetch a list of movies and add them to the movie list
     *
     * @param view The view the button was clicked from.
     */
    public void onClickRefreshListButton(View view) {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetTopMovies().execute();
        } else {
            Toast.makeText(this, "No internet connection. Try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    /** Handles our movie requests and updates the listview.
     */
    private class GetTopMovies extends AsyncTask<String, Void, MovieResultsPage> {

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
        /** Initiates the network call and retrieves the movie list.
         *
         */
        protected MovieResultsPage doInBackground(String... strings) {
            TmdbApi api = new TmdbApi(getString(R.string.rotten_api_key));
            // Grab the top rated movies
            MovieResultsPage movies = api.getMovies().getTopRatedMovies("en", MOVIE_PAGE_LIMIT);

            // Handles storing the images
            String imageBaseUrl = api.getConfiguration().getSecureBaseUrl();
            String imageSizeParam = "w154";
            for (MovieDb movie : movies) {
                String posterUri = imageBaseUrl + imageSizeParam + movie.getPosterPath();
                System.out.println(posterUri);
                Bitmap image = GetImage(posterUri);
                imageUrls.append(movie.getId(), image);
            }

            // Get the top movies
            return movies;
        }

        @Override
        /** When we receive the movies, we need to update the data set for the adapter.
         *
         * @param results The movies that were returned.
         */
        protected void onPostExecute(MovieResultsPage results) {
            super.onPostExecute(results);

            // reset the movieNames and add to them
            movieItems.clear();
            for (MovieDb movie : results) {
                movieItems.add(movie);

                // Notify the adapter of the changes
                theAdapter.notifyDataSetChanged();
            }
        }
    }
}
