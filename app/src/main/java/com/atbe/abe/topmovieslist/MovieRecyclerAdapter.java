package com.atbe.abe.topmovieslist;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atbe.abe.topmovieslist.Activites.MovieMoreInfoActivity;

import java.util.List;

/**
 * Created by abe on 12/25/16.
 */

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MyViewHolder> {
    private List<MovieContainer> mMovies = null;
    private RecyclerView mRecyclerView = null;

    public  boolean isEmpty() { return  mMovies.isEmpty(); }

    public void addMovie(MovieContainer movie) { mMovies.add(movie); }

    public void refreshImages() {
        for (MovieContainer movie : mMovies) {
            if (movie.getPosterImage() == null) {
                movie.GoFetchPosterImage();
            }
        }
    }

    public static final String ARG_MOVIE_INFO_EXTRA = "movie_info_screen_extra";

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_items_list_view, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int pos = mRecyclerView.getChildAdapterPosition(view);
                Toast.makeText(view.getContext(), "You clicked " + mMovies.get(pos).getMovieDb().getTitle(), Toast.LENGTH_SHORT).show();
                Intent movieMoreInfo = new Intent(itemView.getContext(), MovieMoreInfoActivity.class);
                movieMoreInfo.putExtra(ARG_MOVIE_INFO_EXTRA, mMovies.get(pos).getMovieDb());
                itemView.getContext().startActivity(movieMoreInfo);
            }
        });

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MovieContainer movie = mMovies.get(position);
        holder.title.setText(movie.getMovieDb().getTitle());
        holder.description.setText(movie.getMovieDb().getOverview());
        holder.rating.setText(String.valueOf(movie.getMovieDb().getReleaseDate()));
        if (movie.getPosterImage() != null) {
            holder.posterImage.setImageBitmap(movie.getPosterImage());
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

    public MovieRecyclerAdapter(List<MovieContainer> movies, RecyclerView recycler) {
        mMovies = movies;
        mRecyclerView = recycler;
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
