package com.atbe.abe.topmovieslist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MainActivity extends AppCompatActivity {
    // the number of movies you want to get in a single request to their web server
    private static final int MOVIE_PAGE_LIMIT = 10;

    // Temporary list of values
    ArrayList<String> values = new ArrayList<String>();

    // The adapter with the movies
    ArrayAdapter theAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Our listadapter will use the android simple list layout for now
        values.add("Abe");
        theAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                values);

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

        @Override
        /** Initiates the network call and retrieves the movie list.
         *
         */
        protected MovieResultsPage doInBackground(String... strings) {
            TmdbMovies movies = new TmdbApi(getString(R.string.rotten_api_key)).getMovies();

            // Get the top movies
            return movies.getTopRatedMovies("en", MOVIE_PAGE_LIMIT);
        }

        @Override
        /** When we receive the movies, we need to update the data set for the adapter.
         *
         * @param results The movies that were returned.
         */
        protected void onPostExecute(MovieResultsPage results) {
            super.onPostExecute(results);

            // reset the values and add to them
            values.clear();
            for (MovieDb movie : results) {
                values.add(movie.getTitle());
            }

            // Notify the adapter of the changes
            theAdapter.notifyDataSetChanged();
        }
    }
}
