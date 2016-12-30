package com.atbe.abe.topmovieslist.Activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.atbe.abe.topmovieslist.MovieContainer;
import com.atbe.abe.topmovieslist.R;

import info.movito.themoviedbapi.model.MovieDb;

import static com.atbe.abe.topmovieslist.MovieRecyclerAdapter.ARG_MOVIE_INFO_EXTRA;

public class MovieMoreInfoActivity extends AppCompatActivity {
    private MovieContainer mMovie = null;

    private TextView mTitleText, mReleaseDateText, mDescriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_more_info_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initializations
        mDescriptionText = (TextView) findViewById(R.id.movie_more_info_description);
        mReleaseDateText = (TextView) findViewById(R.id.movie_more_info_release_date);
        mMovie = new MovieContainer((MovieDb) getIntent().getSerializableExtra(ARG_MOVIE_INFO_EXTRA),
                this, null);

        // set title to title of movie
        setTitle(mMovie.getMovieDb().getTitle());

        // enables the back arrow button that teleports you back to the home activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetupContentViews();
    }

    private void SetupContentViews() {
        mDescriptionText.setText(mMovie.getMovieDb().getOverview());
        mReleaseDateText.setText(mMovie.getMovieDb().getReleaseDate());
    }
}
