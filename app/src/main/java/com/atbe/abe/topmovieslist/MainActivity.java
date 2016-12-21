package com.atbe.abe.topmovieslist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MainActivity extends AppCompatActivity {

    // the number of movies you want to get in a single request to their web server
    private static final int MOVIE_PAGE_LIMIT = 10;

    // Movies themselves
    private ArrayList<MovieDb> movieItems = new ArrayList<MovieDb>();

    // Image map for the movies
    public static SparseArray<Bitmap> movieImagesArray = new SparseArray<Bitmap>();

    // Trailer links for clicking
    public static SparseArray<String> movieTrailerUrls = new SparseArray<String>();

    // The adapter with the movies
    ArrayAdapter theAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the toolbar
        setSupportActionBar(((Toolbar) findViewById(R.id.my_toolbar)));

        // Movie list adapter needs the movies and the image map
        theAdapter = new MovieListItemAdapter(this, movieItems);

        // Grab the ListView and attach the adapter
        ListView moviesList = (ListView) findViewById(R.id.movie_listview);
        moviesList.setAdapter(theAdapter);

        // Get the initial batch of movies
        new GetTopMovies().execute();
    }

    /** This handler will go fetch a list of movies and add them to the movie list
     *
     * @param view The view the button was clicked from.
     */
    public void onClickRefreshListButton(View view) {
        /*

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        } else {
            Toast.makeText(this, "No internet connection. Try again later.", Toast.LENGTH_SHORT).show();
        }
        */
    }

    /** Called when the movies are retireved and spawns a thread to go out and
     * get each image for each movie.
     */
    public void GetMovieTrailersAndImages() {
        // Goes through each movie received and gets its image
        for (MovieDb movie : movieItems) {
            new GetMovieTrailers().execute(movie);
            new GetMovieImage().execute(movie);
        }
    }

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

    /** Gets the images for our movie items one by one and updates the listview data source.
     */
    private class GetMovieImage extends AsyncTask<MovieDb, Void, SparseArray<Bitmap>> {

        @Override
        /** Initiates the network call and retrieves the image for the movie.
         */
        protected SparseArray<Bitmap> doInBackground(MovieDb... movies) {
            TmdbApi api = new TmdbApi(getString(R.string.rotten_api_key));

            // Handles storing the images
            String imageBaseUrl = api.getConfiguration().getSecureBaseUrl();
            String imageSizeParam = "w154";
            SparseArray<Bitmap> images = new SparseArray<Bitmap>();

            for (MovieDb movie : movies) {
                String posterUri = imageBaseUrl + imageSizeParam + movie.getPosterPath();
                System.out.println(posterUri);
                Bitmap image = GetImage(posterUri);
                images.append(movie.getId(), image);
            }

            return images;
        }

        @Override
        /** When we receive the image, we need to update the data set for the adapter.
         *
         * @param results The movies that were returned.
         */
        protected void onPostExecute(SparseArray<Bitmap> images) {
            super.onPostExecute(images);

            // Process all the movies in-case we got more than one
            for (int i = 0; i < images.size(); i++) {
                movieImagesArray.append(images.keyAt(i), images.valueAt(i));

                // Notify the adapter of the changes
                theAdapter.notifyDataSetChanged();
            }
        }
    }

    /// Used to follow the current page
    private int CurrentMoviePage = 0;

    /** Handles our movie requests and updates the listview.
     * This task does not get
     */
    private class GetTopMovies extends AsyncTask<String, Void, MovieResultsPage> {

        @Override
        /** Initiates the network call and retrieves the movie list.
         */
        protected MovieResultsPage doInBackground(String... strings) {
            TmdbApi api = new TmdbApi(getString(R.string.rotten_api_key));
            // Grab the top rated movies
            return api.getMovies().getTopRatedMovies("en", CurrentMoviePage++);
        }

        @Override
        /** When we receive the movies, we need to update the data set for the adapter.
         *
         * @param results The movies that were returned.
         */
        protected void onPostExecute(MovieResultsPage results) {
            super.onPostExecute(results);

            // reset the movieNames and add to them
//            movieItems.clear();
            for (MovieDb movie : results) {
                movieItems.add(movie);
            }
            // Notify the adapter of the changes
            theAdapter.notifyDataSetChanged();

            // Go get the trailers and images for each movie
            GetMovieTrailersAndImages();
        }
    }

    /** Handles our movie requests and updates the listview.
     * This task does not get
     */
    private class GetMovieTrailers extends AsyncTask<MovieDb, Void, SparseArray<String>> {

        @Override
        /** Initiates the network call and retrieves the movie list.
         */
        protected SparseArray<String> doInBackground(MovieDb... movies) {
            TmdbApi api = new TmdbApi(getString(R.string.rotten_api_key));
            // Grab the top rated movies
            SparseArray<String> trailerUrls = new SparseArray<String>();
            for (MovieDb movie : movies) {
                List<Video> videoList = api.getMovies().getVideos(movie.getId(), "en");
                String youtubeId = "";
                for (Video video : videoList) {
                    if (video.getType().equals("Trailer") && video.getSite().equals("YouTube")) {
                        youtubeId = video.getKey();
                    }
                }

                // Only add the trailer urls which we found
                if (!youtubeId.isEmpty()) {
                    youtubeId = "https://www.youtube.com/watch?v=" + youtubeId;
                    System.out.println("DEBUG: GetMovieTrailers get site = " + youtubeId);

                    trailerUrls.append(movie.getId(), youtubeId);
                }
            }

            return trailerUrls;
        }

        @Override
        /** When we receive the movies, we need to update the data set for the adapter.
         *
         * @param results The movies that were returned.
         */
        protected void onPostExecute(SparseArray<String> urls) {
            super.onPostExecute(urls);

            // reset the movieNames and add to them
//            movieItems.clear();
            for (int i = 0; i < urls.size(); i++) {
                System.out.println("DEBUG: GetMovieTrailers-onPostExecute URL Adding " + urls.valueAt(i));
                movieTrailerUrls.append(urls.keyAt(i), urls.valueAt(i));

                // Notify the adapter of the changes
                theAdapter.notifyDataSetChanged();
            }
        }

    }
}
