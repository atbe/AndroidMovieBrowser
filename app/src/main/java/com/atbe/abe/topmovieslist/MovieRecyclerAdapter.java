package com.atbe.abe.topmovieslist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abe on 12/25/16.
 */

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MyViewHolder> {
    List<MovieContainer> mMovies;

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
        holder.rating.setText(String.valueOf(movie.getmMovie().getVoteAverage()));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, rating;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.movie_listview_title);
            description = (TextView) view.findViewById(R.id.movie_listview_description);
            rating = (TextView) view.findViewById(R.id.movie_listview_rating);

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
//
//MoviesAdapter.java
//        package info.androidhive.recyclerview;
//
//        import android.support.v7.widget.RecyclerView;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.TextView;
//
//        import java.util.List;
//
//public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {
//
//    private List<Movie> moviesList;
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public TextView title, year, genre;
//
//        public MyViewHolder(View view) {
//            super(view);
//            title = (TextView) view.findViewById(R.id.title);
//            genre = (TextView) view.findViewById(R.id.genre);
//            year = (TextView) view.findViewById(R.id.year);
//        }
//    }
//
//
//    public MoviesAdapter(List<Movie> moviesList) {
//        this.moviesList = moviesList;
//    }
//
//    @Override
//    public MyViewHolder (ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.movie_list_row, parent, false);
//
//        return new MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        Movie movie = moviesList.get(position);
//        holder.title.setText(movie.getTitle());
//        holder.genre.setText(movie.getGenre());
//        holder.year.setText(movie.getYear());
//    }
//
//    @Override
//    public int getItemCount() {
//        return moviesList.size();
//    }
//}