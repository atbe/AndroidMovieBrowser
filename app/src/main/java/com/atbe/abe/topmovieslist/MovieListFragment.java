package com.atbe.abe.topmovieslist;

/**
 * Created by abe on 12/24/16.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

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

    public void GoGetMovies() {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            if (mRecyclerviewAdapter.isEmpty()) {
                new GetMovies(mRecyclerviewAdapter).execute(mMovieOption);
            } else {
                // we have movies, check on the images
                mRecyclerviewAdapter.refreshImages();
            }
        } else {
            Toast.makeText(getContext(), "No internet connection available. Please refresh.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        System.out.println("DEBUG: MovieListFragment(onActivityCreated) - called.");
        super.onActivityCreated(savedInstanceState);

        // Go get the movies based on the option
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerviewAdapter = new MovieRecyclerAdapter(new ArrayList<MovieContainer>(), mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerviewAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        GoGetMovies();
    }

    public MovieListFragment() {
        System.out.println("DEBUG: MovieListFragment - Constructor called...");
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MovieListFragment newInstance(int optionInt) {
        System.out.println("DEBUG: MovieListFragment(newInstance) - called.");
        MovieOptions option;
        switch(optionInt) {
            case 0:
                option = MovieOptions.NowPlayingMovies;
                break;
            case 1:
                option = MovieOptions.UpcomingMovies;
                break;
            case 2:
                option = MovieOptions.TopRatedMovies;
                break;
            default:
                System.out.println("DEBUG: MovieListFragment(newInstance switch) - default!! = " + optionInt);
                option = MovieOptions.NowPlayingMovies;
        }
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
            System.out.println("DEBUG: GetMovies(doInBackground) - Getting " + option.toString());
            switch (option) {
                case NowPlayingMovies:
                    return api.getMovies().getNowPlayingMovies("en", CurrentMoviePage);
                case UpcomingMovies:
                    // TODO: Fix this call to grab upcoming movies from the current date
                    return api.getMovies().getUpcoming("en", CurrentMoviePage);
                case TopRatedMovies:
                    return api.getMovies().getTopRatedMovies("en", CurrentMoviePage);
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
                        //System.out.println("DEBUG: GetMovies - Adding movie " + movie.getTitle());
                        adapter.addMovie(new MovieContainer(movie, getActivity(), mRecyclerviewAdapter));
                    }
                    mRecyclerviewAdapter.notifyDataSetChanged();
                }
            }
            // TODO Get trailers and posters
        }
    }
}
