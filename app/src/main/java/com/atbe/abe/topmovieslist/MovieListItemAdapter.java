package com.atbe.abe.topmovieslist;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by abe on 12/19/16.
 */

/** Custom adapter for the movie items in our list.
 */
public class MovieListItemAdapter extends ArrayAdapter<MovieDb> {

    public MovieListItemAdapter(Context context, ArrayList<MovieDb> movies) {
        super(context, R.layout.movie_items_list_view, movies);
    }

    @NonNull
    @Override
    /** Getter for a list item
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater movieInflater = LayoutInflater.from(getContext());
            convertView = movieInflater.inflate(R.layout.movie_items_list_view, parent, false);
        }

        // Get the current movie item
        final MovieDb movieItem = getItem(position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert movieItem != null;
                Integer urlIndex = MainActivity.movieTrailerUrls.indexOfKey(movieItem.getId());
                if (urlIndex > -1) {
                    // We have a trailer link
                    Intent movieWebViewIntent = new Intent(getContext(), MovieWebPageActivity.class);
                    String url = MainActivity.movieTrailerUrls.valueAt(urlIndex);

                    System.out.println("DEBUG: MovieListItemAdapter opening url = " + url);

                    movieWebViewIntent.putExtra("url", url);
                    getContext().startActivity(movieWebViewIntent);
                } else {
                    Toast.makeText(getContext(),
                            "Sorry, we could not find a trailer for this movie", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // The title textview
        ((TextView) convertView.findViewById(R.id.movie_listview_title))
                .setText(movieItem.getTitle());

        // The rating textview
        ((TextView) convertView.findViewById(R.id.movie_listview_rating))
                .setText(String.valueOf(movieItem.getVoteAverage()));

        // The movie cover art
        // TODO: no fallback image yet. Implement one if the poster is not found.
        ((ImageView) convertView.findViewById(R.id.movie_listview_image))
                .setImageBitmap(MainActivity.movieImagesArray.get(movieItem.getId()));

        ((TextView) convertView.findViewById(R.id.movie_listview_description))
                .setText(movieItem.getOverview());

        return convertView;
    }
}
