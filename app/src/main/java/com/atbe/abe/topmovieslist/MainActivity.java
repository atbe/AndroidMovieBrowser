package com.atbe.abe.topmovieslist;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    // the Rotten Tomatoes API key of your application! get this from their website
    private static final String API_KEY = Resources.getSystem().getString(R.string.rotten_api_key);

    // the number of movies you want to get in a single request to their web server
    private static final int MOVIE_PAGE_LIMIT = 10;

    private ListView moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickRefreshListButton(View view) {
    }
}
