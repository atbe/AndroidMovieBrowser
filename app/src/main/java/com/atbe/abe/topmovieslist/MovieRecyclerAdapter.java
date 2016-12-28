package com.atbe.abe.topmovieslist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abe on 12/25/16.
 */

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MyViewHolder> {
    private List<MovieContainer> mMovies;

    public  boolean isEmpty() { return  mMovies.isEmpty(); }

    public void addMovie(MovieContainer movie) { mMovies.add(movie); }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_items_list_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MovieContainer movie = mMovies.get(position);
        holder.title.setText(movie.getmMovie().getTitle());
        holder.description.setText(movie.getmMovie().getOverview());
        holder.rating.setText(String.valueOf(movie.getmMovie().getReleaseDate()));
        if (movie.getmPosterImage() != null) {
            holder.posterImage.setImageBitmap(movie.getmPosterImage());
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, rating;
        public ImageView posterImage;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.movie_listview_title);
            description = (TextView) view.findViewById(R.id.movie_listview_description);
            rating = (TextView) view.findViewById(R.id.movie_listview_rating);
            posterImage = (ImageView) view.findViewById(R.id.movie_listview_poster);

        }
    }

    public MovieRecyclerAdapter(List<MovieContainer> movies) {
        mMovies = movies;
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
