package com.atbe.abe.topmovieslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by abe on 12/19/16.
 */

public class MovieListItemAdapter extends ArrayAdapter<MovieDb> {

    public MovieListItemAdapter(Context context, ArrayList<MovieDb> movies) {
        super(context, R.layout.movie_items_list_view, movies);
    }

    @NonNull
    @Override
    /** Getter for a list item
     *
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater movieInflater = LayoutInflater.from(getContext());

        View movieView = movieInflater.inflate(R.layout.movie_items_list_view, parent, false);

        // Get the current movie item
        MovieDb movieItem = getItem(position);

        // The title textview
        TextView movieTitle = (TextView) movieView.findViewById(R.id.movie_listview_title);
        movieTitle.setText(movieItem.getTitle());

        // The rating textview
        TextView ratingView = (TextView) movieView.findViewById(R.id.movie_listview_rating);
        System.out.println(movieItem.getVoteCount());
        String rating = String.valueOf(movieItem.getVoteAverage());
        ratingView.setText(rating);

        // The movie cover art
        ImageView moviePoster = (ImageView) movieView.findViewById(R.id.movie_listview_image);

        // TODO: no fallback image yet. Implement one if the poster is not found.
        moviePoster.setImageBitmap(MainActivity.imageUrls.get(movieItem.getId()));

        return movieView;
    }
}
