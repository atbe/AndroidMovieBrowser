package com.atbe.abe.topmovieslist;

/**
 * Created by abe on 12/24/16.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

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
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_MOVIE_OPTION = "movie_option";

    private RecyclerView mRecyclerView;
    private MovieRecyclerAdapter mRecyclerviewAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private MovieOptions mMovieOption = null;

    public void setmMovieOption(MovieOptions mMovieOption) {
        this.mMovieOption = mMovieOption;
    }

    public enum MovieOptions {
        NowPlayingMovies, TopRatedMovies, UpcomingMovies;
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        System.out.println("DEBUG: MovieListFragment(onActivityCreated) - called.");
        super.onActivityCreated(savedInstanceState);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerviewAdapter = new MovieRecyclerAdapter(new ArrayList<MovieContainer>());
        mRecyclerView.setAdapter(mRecyclerviewAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Go get the movies based on the option
        if (mMovieOption != null) {
            new GetMovies(mRecyclerviewAdapter).execute(mMovieOption);
        }

    }

    public MovieListFragment() {
        System.out.println("DEBUG: MovieListFragment - Constructor called...");
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MovieListFragment newInstance(MovieOptions option) {
        System.out.println("DEBUG: MovieListFragment(newInstance) - called.");
        System.out.println("DEBUG: MovieListFragment(newInstance) - option = " + option.toString());

        MovieListFragment fragment = new MovieListFragment();
        fragment.setmMovieOption(option);

        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE_OPTION, option);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("DEBUG: MovieListFragment(onCreateView) - called.");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        return rootView;
    }

    /// Used to follow the current page
    private int CurrentMoviePage = 0;

    /** Handles our movie requests and updates the listview.
     * This task does not get
     */
    private class GetMovies extends AsyncTask<MovieOptions, Void, MovieResultsPage> {
        WeakReference<MovieRecyclerAdapter> mAdapter;

        public GetMovies(MovieRecyclerAdapter adapter) {
            mAdapter = new WeakReference<MovieRecyclerAdapter>(adapter);
            System.out.println("DEBUG: MovieListFragment(GetMovies) - Constructor called.");
        }

        @Override
        /** Initiates the network call and retrieves the movie list.
         */
        protected MovieResultsPage doInBackground(MovieOptions... options) {
            TmdbApi api = new TmdbApi(getString(R.string.rotten_api_key));
            // Grab the top rated movies

            MovieOptions option = options[0];
            switch (option) {
                case NowPlayingMovies:
                    return api.getMovies().getNowPlayingMovies("en", CurrentMoviePage);
                case UpcomingMovies:
                    return api.getMovies().getUpcoming("en", CurrentMoviePage);
            }

            // TODO: Is there a way around this silly return?
            return null;
        }

        @Override
        /** When we receive the movies, we need to update the data set for the adapter.
         *
         * @param results The movies that were returned.
         */
        protected void onPostExecute(MovieResultsPage results) {
            super.onPostExecute(results);

            if (mAdapter != null && results != null && isAdded()) {
                final MovieRecyclerAdapter adapter = mAdapter.get();
                if (adapter != null) {
                    // reset the movieNames and add to them
                    for (MovieDb movie : results) {
                        System.out.println("DEBUG: GetMovies - Adding movie " + movie.getTitle());
                        adapter.addMovie(new MovieContainer(movie));
                    }


                    mRecyclerviewAdapter.notifyDataSetChanged();
                }
            }
            // TODO Get trailers and posters
        }
    }
}
